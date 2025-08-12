package com.ex.tjspring.study.controller;


import com.ex.tjspring.common.service.S3DirKey;
import com.ex.tjspring.common.service.S3Service;
import com.ex.tjspring.study.dto.GroupDto;
import com.ex.tjspring.study.dto.UserRequestDto;
import com.ex.tjspring.study.service.GroupSubscriptionStatus;
import com.ex.tjspring.study.service.UserRequestService;
import com.ex.tjspring.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/userRequest")
public class UserRequestController {

    private final UserRequestService userRequestService;
    private final S3Service s3Service;


    // ### 유저가 해당 스터디그룹에 가입 또는 가입신청 한 적이 있는지...
    @GetMapping("/exist/{groupId}/{userId}/{studyPostId}")
    public ResponseEntity<Map<String, Object>> checkUserStatusForApplication(@PathVariable Long groupId,
                                                                              @PathVariable Long userId ,
                                                                              @PathVariable Long studyPostId) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean exists = userRequestService.checkUserStatusForApplication(groupId, userId,studyPostId);
            result.put("exists", exists);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.info("####:= {}" , e.getMessage());
            result.put("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    // ## 가입신청할 스터디 그룹정보 가져오기..
    @GetMapping("/group/{groupId}")
    public ResponseEntity<Map<String, Object>> selectStudyGroupFindByGroupId(@PathVariable Long groupId) {
        Map<String, Object> result = new HashMap<>();
        try {
            GroupDto groupDto = userRequestService.selectStudyGroupFindByGroupId(groupId);
            if (groupDto != null) {

                if (groupDto.getThumbnail() == null || groupDto.getThumbnail().isEmpty()) {
                    groupDto.setThumbnail("/images/default-thumbnail.png");
                } else {
                    // S3 그룹이미지 경로 setting....
                    groupDto.setThumbnail(s3Service.getFileFullPath(S3DirKey.STUDYGROUPIMG,groupDto.getThumbnail()));
                }

                result.put("groupDto", groupDto);
                return ResponseEntity.ok(result);
            } else {
                result.put("message", "해당 그룹을 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            result.put("error", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }



    // ### 가입신청 등록
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> insertUserRequest(@Valid @RequestBody UserRequestDto userRequestDto) {
        Map<String, Object> result = new HashMap<>();
        try {

            log.info("application group status: {} ", GroupSubscriptionStatus.PENDING.name());
            userRequestDto.setApplicationStatus(GroupSubscriptionStatus.PENDING.name()); // 가입대기상태
            userRequestService.insertUserRequest(userRequestDto);
            result.put("message", "신청이 성공적으로 접수되었습니다.");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("error", "신청 처리에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    //-------------------------------------------------

    // 가입신청 목록 O
    @GetMapping("/list/{groupId}")
    public ResponseEntity<Map<String, Object>> selectUserRequestsByGroupId(@PathVariable Long groupId) {
        List<UserRequestDto> list = userRequestService.selectUserRequestFindByGroupId(groupId);

        Map<String, Object> response = new HashMap<>();

        if (list.isEmpty()) {
            response.put("message", "해당 그룹에 대한 신청 내역이 없습니다.");
        } else {
            response.put("message", "신청 내역 조회 성공");
        }
        response.put("list", list);
        return ResponseEntity.ok(response);
    }

    // 가입신청 승인 처리 O
    @PostMapping("/approve/{id}")
    public ResponseEntity<Map<String, Object>> approveUserRequest(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            userRequestService.approveUserRequest(id);
            result.put("message", "가입 요청이 승인되었습니다.");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("error", "승인 처리 중 오류: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    // 가입신청 거절 처리
    @PostMapping("/reject/{id}")
    public ResponseEntity<Map<String, Object>> rejectUserRequest(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            userRequestService.rejectUserRequest(id);
            result.put("message", "가입 요청이 거절되었습니다.");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("error", "거절 처리 중 오류: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }





//    // 가입요청 수정
//    @PutMapping("/update/{id}")
//    public ResponseEntity<String> updateUserRequest(@PathVariable Long id, @Valid @RequestBody UserRequestDto userRequestDto) {
//        // DTO의 ID와 PathVariable의 ID가 일치하는지 확인
//        if (!id.equals(userRequestDto.getId())) {
//            return ResponseEntity.badRequest().body("URL의 ID와 요청 본문의 ID가 일치하지 않습니다.");
//        }
//
//        try {
//            userRequestService.updateUserRequest(userRequestDto);
//            return ResponseEntity.ok("가입요청이 성공적으로 수정되었습니다.");
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("가입요청 수정에 실패했습니다: " + e.getMessage());
//        }
//    }

    // 가입요청 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserRequest(@PathVariable Long id) {
        try {
            int result = userRequestService.deleteUserRequest(id);
            if (result > 0) {
                return ResponseEntity.ok("가입요청이 성공적으로 삭제되었습니다.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("가입요청 삭제에 실패했습니다: " + e.getMessage());
        }
    }

}
