package com.ex.tjspring.study.controller;


import com.ex.tjspring.common.dto.AttachFileDto;
import com.ex.tjspring.common.service.AttachFileService;
import com.ex.tjspring.common.service.S3DirKey;
import com.ex.tjspring.common.service.S3Service;
import com.ex.tjspring.editorexample.dto.StudyPostInsertDto;
import com.ex.tjspring.editorexample.dto.StudyPostUpdateDto;
import com.ex.tjspring.study.dto.GroupNickDto;
import com.ex.tjspring.study.dto.StudyPostDto;
import com.ex.tjspring.study.dto.StudyPostViewDto;
import com.ex.tjspring.study.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study-groups")
public class StudyPostController {

    private final StudyPostService studyPostService;
    private final AttachFileService attachFileService;
    private final S3Service s3Service;

    // user가 개설한 스터디 그룹 목록 가져오기
    @GetMapping( value = "/user/{userId}")
    public ResponseEntity<Map<String, Object>> selectStudyGroupsFindByUserId(@PathVariable Long userId) {
        List<GroupNickDto> list = studyPostService.selectGroupsFindByUserId(userId);

        System.out.println("################## user study groups #################");
        log.info( "userId = {}" , userId );
        if (list == null || list.isEmpty()) {
            Map<String, Object> map = new HashMap<>();
            map.put("list", Collections.emptyList()); // Collections.emptyList()로 빈 리스트 반환
            return ResponseEntity.ok(map);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("groupList", list);
        map.put("totalCount", list.size());
        return ResponseEntity.ok(map);
    }

    @GetMapping("/post/{groupId}/exist")
    public ResponseEntity<Map<String, Boolean>> existsPostByGroupId(@PathVariable("groupId") Long groupId) {

        log.info("####### exist #######");
        boolean exists = studyPostService.existsPostByGroupId(groupId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exist", exists);

        return ResponseEntity.ok(response);
    }

    // 스터디그룹 정보 , 홍보글 1개 가져오기
    @GetMapping("/post/{groupId}")
    public ResponseEntity<Map<String,Object>> selectPostFindByGroupId(@PathVariable Long groupId) {

        log.info("####### post #######");
        Map<String, Object> map = new HashMap<>();

        GroupNickDto groupDto = studyPostService.selectGroupsFindByGroupId(groupId);
        log.info( "groupId = {}" , groupId );
        if (groupDto == null) {
            return ResponseEntity.notFound().build();
        }

        map.put("groupDto", groupDto);

        StudyPostViewDto studyPostViewDto = studyPostService.selectPostFindByGroupId(groupId);
        if (studyPostViewDto == null) {
            return ResponseEntity.ok(map);
        }
        String profileImage = studyPostViewDto.getProfileImage();
        log.info("####### profileImage = {} " ,profileImage);
        log.info("####### getAuthorId = {} " ,studyPostViewDto.getAuthorId());
        //if (profileImage.isEmpty()) {
        if (profileImage == null || profileImage.isEmpty() || profileImage.contains("profile") ) {
            studyPostViewDto.setProfileImage("/images/default-profile.png");
        } else {
            studyPostViewDto.setProfileImage( s3Service.getFileFullPath(S3DirKey.MYPROFILEIMG, profileImage) );
        }


        map.put("postDto", studyPostViewDto);
        return ResponseEntity.ok(map);
    }


    @PostMapping("/post")
    public ResponseEntity<Long> insertPost(@Validated @ModelAttribute StudyPostInsertDto dto ,
                                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            // 게시글 등록
            Long newPostId = insertPostToDB(dto);
            List<MultipartFile> attachments = dto.getAttachments();

            // 첨부파일 처리
            if (attachments != null && !attachments.isEmpty()) {
                handleFileUpload(newPostId, attachments);
            }

            // newPostId 직접 반환
            return ResponseEntity.ok(newPostId);

        } catch (Exception e) {
            log.error("게시글 등록 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/promotion/edit")
    public ResponseEntity<?> updatePost( @ModelAttribute StudyPostUpdateDto updateDto ) throws IOException {

        // log.info("@@@ ExEditorEditDto = {} " , updateDto );

        if (updateDto.getStudyPostId() == null) {
            return ResponseEntity.badRequest().body("ID는 필수입니다.");
        }

        try {
            // 수정된 게시글 update
            updatePostToDB(updateDto);
            // 삭제한 attach 파일 처리
            deleteAttachFiles(updateDto);
            // 새로 추가된 attach file 처리
            handleFileUpload(updateDto.getStudyPostId(), updateDto.getNewAttachments());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            log.error("게시글 수정 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @DeleteMapping("/promotion/{groupId}")
    public ResponseEntity<?> deletePost(@PathVariable Long groupId) {

        StudyPostViewDto viewDto = studyPostService.selectPostFindByGroupId(groupId);
        Long postId = viewDto.getStudyPostId();

        if (viewDto == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            // 홍보글 첨부파일 삭제
            deletePostAttachFiles(viewDto);
            // 게시글을 삭제합니다.
            // (첨부파일 메타데이터는 FK cascade에 의해 자동으로 삭제됩니다.)
            studyPostService.deletePost(postId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            log.error("게시글 삭제 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }



    private Long insertPostToDB(StudyPostInsertDto insertDto ) {
        StudyPostDto dto = new StudyPostDto();

        dto.setTitle(insertDto.getTitle());
        dto.setContent(insertDto.getContent());
        dto.setGroupId(insertDto.getGroupId());
        dto.setAuthorId(insertDto.getAuthorId());
        dto.setCreatedAt(LocalDateTime.now());
        dto.setRecruitStartDate(insertDto.getRecruitStartDate());
        dto.setRecruitEndDate(insertDto.getRecruitEndDate());
        dto.setUpdatedAt(LocalDateTime.now());
        dto.setViewCount(0);
        dto.setHashTag(insertDto.getHashTag());

        return studyPostService.insertPost(dto);
    }

    private void updatePostToDB( StudyPostUpdateDto putDto) {
        StudyPostDto dto = new StudyPostDto();
        dto.setStudyPostId(putDto.getStudyPostId());
        dto.setTitle(putDto.getTitle());
        dto.setContent(putDto.getContent());
        dto.setAuthorId(putDto.getAuthorId());
        dto.setRecruitStartDate(putDto.getRecruitStartDate());
        dto.setRecruitEndDate(putDto.getRecruitEndDate());
        dto.setUpdatedAt(LocalDateTime.now());
        dto.setHashTag(putDto.getHashTag());
        studyPostService.updatePost(dto);
    }

    // 첨부파일 추가 S3 upload
    private void handleFileUpload(Long postId, List<MultipartFile> attachments) throws IOException {

        // 새 첨부파일 추가 처리 (S3 및 DB)
        if( attachments != null && !attachments.isEmpty()) {

            for (MultipartFile file : attachments) {
                if (!file.isEmpty()) {
                    String originalFileName = file.getOriginalFilename();
                    String storedFileName = s3Service.upload(S3DirKey.ATTACHFILE, file);

                    // upload 한 file url...
                    //String storedFileFullPath = s3Service.getFileFullPath(S3DirKey.ATTACHFILE, storedFileName);

                    AttachFileDto attachFileDto = new AttachFileDto();
                    attachFileDto.setPostId(postId);
                    attachFileDto.setFileName(originalFileName);
                    attachFileDto.setStoredFileName(storedFileName);
                    attachFileDto.setFileSize(file.getSize());
                    attachFileDto.setFileType(file.getContentType());
                    attachFileDto.setRegDate(LocalDateTime.now());

                    attachFileService.insert(attachFileDto);
                }
            }// for
        }
    }



    // 수정시 attachFile 개별 삭제
    private void deleteAttachFiles(StudyPostUpdateDto updateDto) {
        // 기존 첨부파일 삭제 처리 (DB 및 S3)
        List<String> deletedFileNames = updateDto.getDeletedStoredFileNames();
        //log.info("###### deletedFileNames={} " ,deletedFileNames);
        if (deletedFileNames != null && !deletedFileNames.isEmpty()) {
            for (String storedFileName : deletedFileNames) {
                try {
                    // S3에서 파일 삭제
                    s3Service.delete( S3DirKey.ATTACHFILE ,storedFileName);
                    // DB에서 파일 메타데이터 삭제
                    attachFileService.deleteByStoredFileName(storedFileName);
                    //log.info("S3 및 DB에서 파일 삭제 완료: {}", storedFileName);
                } catch (Exception e) {
                    //log.error("S3/DB에서 파일 {} 삭제 실패: {}", storedFileName, e.getMessage(), e);
                    // 부분 실패를 허용하거나, 트랜잭션 롤백 전략을 고려해야 합니다.
                }
            }
        }
    }

    // 홍보글 삭제시 해당 포스트의 첨부파일 일괄 삭제
    private void deletePostAttachFiles(StudyPostViewDto dto) {

        if (dto != null && dto.getAttachFile() != null) {
            for (AttachFileDto file : dto.getAttachFile()) {
                try {
                    // S3에서 파일 삭제
                    s3Service.delete(S3DirKey.ATTACHFILE,file.getStoredFileName());
                    log.info("게시글 {}에 연결된 S3 파일 삭제 완료: {}", dto.getStudyPostId() , file.getStoredFileName());
                } catch (Exception e) {
                    log.error("게시글 {}에 연결된 S3 파일 {} 삭제 실패: {}", dto.getStudyPostId() , file.getStoredFileName(), e.getMessage(), e);
                }
            }
        }
    }

}
