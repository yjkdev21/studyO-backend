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
    private final S3Service s3Service; // S3Service는 여전히 S3 경로를 가져오는 데 필요합니다.

    // user가 개설한 스터디 그룹 목록 가져오기
    @GetMapping( value = "/user/{userId}")
    public ResponseEntity<Map<String, Object>> selectStudyGroupsFindByUserId(@PathVariable Long userId) {
        List<GroupNickDto> list = studyPostService.selectGroupsFindByUserId(userId);

        System.out.println("################## user study groups #################");
        //log.info( "userId = {}" , userId );
        if (list == null || list.isEmpty()) {
            Map<String, Object> map = new HashMap<>();
            map.put("groupList", Collections.emptyList()); // Collections.emptyList()로 빈 리스트 반환
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

        log.info( "groupId = {}" , groupId );
        GroupNickDto groupDto = studyPostService.selectGroupsFindByGroupId(groupId);

        if (groupDto == null) {
            return ResponseEntity.notFound().build();
        }

        log.info( "groupDto put = {}" , groupId );
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


    /**
     * 새로운 게시글을 등록하고 첨부파일을 처리합니다.
     * 모든 비즈니스 로직은 서비스 계층으로 이동되었습니다.
     */
    @PostMapping("/post")
    public ResponseEntity<Long> insertPost(@Validated @ModelAttribute StudyPostInsertDto dto,
                                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.error("게시글 등록 유효성 검사 실패: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(null);
        }

        try {
            Long newPostId = studyPostService.insertPostWithFiles(dto);
            log.info("#### 게시글 등록 완료. newPostId: {}", newPostId);
            return ResponseEntity.ok(newPostId);

        } catch (Exception e) {
            log.error("게시글 등록 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 기존 게시글을 수정하고 첨부파일을 업데이트합니다.
     * 모든 비즈니스 로직은 서비스 계층으로 이동되었습니다.
     */
    @PostMapping("/post/edit")
    public ResponseEntity<?> updatePost(@ModelAttribute StudyPostUpdateDto updateDto) throws IOException {

        //log.info("@@@ ExEditorEditDto = {} ", updateDto);

        if (updateDto.getStudyPostId() == null) {
            return ResponseEntity.badRequest().body("ID는 필수입니다.");
        }

        try {
            studyPostService.updatePostWithFiles(updateDto);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            log.error("게시글 수정 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @DeleteMapping("/post/{groupId}")
    public ResponseEntity<?> deletePost(@PathVariable Long groupId) {

        StudyPostViewDto viewDto = studyPostService.selectPostFindByGroupId(groupId);
        if (viewDto == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            studyPostService.deletePostWithFiles(viewDto);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            log.error("게시글 삭제 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/increment-view/{studyPostId}")
    public ResponseEntity<String> increasePostViewCnt(@PathVariable Long studyPostId) {
        boolean success = studyPostService.increasePostViewCnt(studyPostId);
        if (success) {
            return ResponseEntity.ok("View count incremented.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Study post not found.");
        }
    }


    @GetMapping("/bookmark-count/{groupId}")
    public int selectFindByGroupIdBookmarkCnt(@PathVariable Long groupId) {
        return studyPostService.selectFindByGroupIdBookmarkCnt(groupId);
    }


}
