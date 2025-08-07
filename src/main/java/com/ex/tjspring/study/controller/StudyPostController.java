package com.ex.tjspring.study.controller;

import com.ex.tjspring.common.service.S3DirKey;
import com.ex.tjspring.common.service.S3Service;
import com.ex.tjspring.editorexample.dto.StudyPostInsertDto;
import com.ex.tjspring.editorexample.dto.StudyPostUpdateDto;
import com.ex.tjspring.study.dto.GroupNickDto;
import com.ex.tjspring.study.dto.StudyPostViewDto;
import com.ex.tjspring.study.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final S3Service s3Service;

    // user가 개설한 스터디 그룹 목록 가져오기
    @GetMapping( value = "/user/{userId}")
    public ResponseEntity<Map<String, Object>> selectStudyGroupsFindByUserId(@PathVariable Long userId) {
        List<GroupNickDto> list = studyPostService.selectGroupsFindByUserId(userId);

        System.out.println("################## user study groups #################");
        log.info( "userId = {}" , userId );
        if (list == null || list.isEmpty()) {
            Map<String, Object> map = new HashMap<>();
            map.put("list", Collections.emptyList());
            return ResponseEntity.ok(map);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("groupList", list);
        map.put("totalCount", list.size());
        return ResponseEntity.ok(map);
    }

    // 특정 스터디그룹에 홍보 게시글이 존재하는지 확인
    @GetMapping("/post/{groupId}/exist")
    public ResponseEntity<Map<String, Boolean>> existsPostByGroupId(@PathVariable("groupId") Long groupId) {

        log.info("####### exist #######");
        boolean exists = studyPostService.existsPostByGroupId(groupId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exist", exists);

        return ResponseEntity.ok(response);
    }

    // 스터디그룹 정보 및 홍보글 상세 조회
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
        // profileImage 경로 처리
        if (profileImage == null || profileImage.isEmpty() || profileImage.contains("profile") ) {
            studyPostViewDto.setProfileImage("/images/default-profile.png");
        } else {
            // S3Service를 사용하여 전체 경로 생성
            studyPostViewDto.setProfileImage( s3Service.getFileFullPath(S3DirKey.MYPROFILEIMG, profileImage) );
        }

        map.put("postDto", studyPostViewDto);
        return ResponseEntity.ok(map);
    }

    // 게시글 등록
    @PostMapping("/post")
    public ResponseEntity<Long> insertPost(@Validated @ModelAttribute StudyPostInsertDto dto ,
                                           BindingResult bindingResult ,
                                           @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments ) {

        log.info("### newPost ######");
        if (bindingResult.hasErrors()) {
            for (ObjectError o : bindingResult.getAllErrors()) {
                log.error(o.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(null);
        }
        log.info("### newPost try ######");

        try {
            // 서비스 계층의 트랜잭션 메서드 호출
            Long newPostId = studyPostService.createPostWithAttachments(dto, attachments);
            log.info("### newPostId = {}", newPostId);

            return ResponseEntity.ok(newPostId);

        } catch (Exception e) {
            log.error("게시글 등록 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // 게시글 수정
    @PostMapping("/promotion/edit")
    public ResponseEntity<?> updatePost( @ModelAttribute StudyPostUpdateDto updateDto ) throws IOException {

        if (updateDto.getStudyPostId() == null) {
            return ResponseEntity.badRequest().body("ID는 필수입니다.");
        }

        try {
            // 서비스 계층의 트랜잭션 메서드 호출
            studyPostService.updatePost(updateDto);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            log.error("게시글 수정 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // 게시글 삭제
    @DeleteMapping("/post/{groupId}")
    public ResponseEntity<?> deletePost(@PathVariable Long groupId) {

        log.info("######  deletePost ######");
        StudyPostViewDto viewDto = studyPostService.selectPostFindByGroupId(groupId);

        if (viewDto == null) {
            return ResponseEntity.notFound().build();
        }

        Long postId = viewDto.getStudyPostId();

        try {
            // 서비스 계층의 트랜잭션 메서드 호출
            studyPostService.deletePost(postId); // deletePost 메서드에서 첨부파일 삭제까지 처리
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            log.error("게시글 삭제 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}