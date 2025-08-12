package com.ex.tjspring.study.controller;

import com.ex.tjspring.common.service.S3DirKey;
import com.ex.tjspring.common.service.S3Service;
import com.ex.tjspring.study.dto.GroupDto;
import com.ex.tjspring.study.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestPart;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/study")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class GroupController {

    @Autowired
    private GroupService groupService;
    @Autowired
    private S3Service s3Service;


    // ========== 새로 추가: 사용자 닉네임 조회 ==========
    @GetMapping("/user/{userId}/nickname")
    public ResponseEntity<Map<String, Object>> getUserNickname(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            String nickname = groupService.getUserNickname(userId);

            if (nickname == null) {
                response.put("success", false);
                response.put("message", "사용자 정보를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }

            response.put("success", true);
            response.put("userId", userId);
            response.put("nickname", nickname);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("사용자 닉네임 조회 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "닉네임 조회 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> createGroup(
            @RequestPart("groupDto") GroupDto groupDto,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            String storedFileName = null;
            String thumbnailFullPath = null;

            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                storedFileName = s3Service.upload(S3DirKey.STUDYGROUPIMG, thumbnailFile);
                if (storedFileName != null) {
                    thumbnailFullPath = s3Service.getFileFullPath(S3DirKey.STUDYGROUPIMG, storedFileName);
                }
            }

            groupDto.setThumbnail(storedFileName);
            groupDto.setThumbnailFullPath(thumbnailFullPath);

            Long groupId = groupService.insertWithMembership(groupDto);

            response.put("success", true);
            response.put("groupId", groupId);
            response.put("thumbnailFullPath", thumbnailFullPath);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("스터디 그룹 생성 오류", e);
            response.put("success", false);
            response.put("message", "스터디 그룹 등록 실패");
            return ResponseEntity.internalServerError().body(response);
        }
    }


    @GetMapping("/{groupId}")
    public ResponseEntity<Map<String, Object>> getGroup(@PathVariable Long groupId) {
        Map<String, Object> response = new HashMap<>();

        try {
            GroupDto group = groupService.selectGroupById(groupId);

            if (group == null) {
                return ResponseEntity.notFound().build();
            }

            String thumbnail = group.getThumbnail();
            if (thumbnail == null || thumbnail.isEmpty() || thumbnail.contains("default")) {
                group.setThumbnailFullPath("/images/default-thumbnail.png");
            } else {
                group.setThumbnailFullPath(s3Service.getFileFullPath(S3DirKey.STUDYGROUPIMG, thumbnail));
            }

            response.put("success", true);
            response.put("data", group);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("스터디 그룹 조회 오류", e);
            response.put("success", false);
            response.put("message", "스터디 그룹 조회 실패");
            return ResponseEntity.internalServerError().body(response);
        }
    }


    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Long id,
            @RequestPart("groupDto") @Validated GroupDto groupDto,
            BindingResult bindingResult,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (bindingResult.hasErrors()) {
                response.put("success", false);
                response.put("message", "필수 필드를 모두 입력해주세요.");
                return ResponseEntity.badRequest().body(response);
            }

            // 이미지가 있으면 S3에 업로드 후 groupDto에 저장
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                String storedFileName = s3Service.upload(S3DirKey.STUDYGROUPIMG, thumbnailFile);
                groupDto.setThumbnail(storedFileName);
                String thumbnailFullPath = s3Service.getFileFullPath(S3DirKey.STUDYGROUPIMG, storedFileName);
                groupDto.setThumbnailFullPath(thumbnailFullPath);
            } else {
                // 만약 기존 이미지 유지할 경우, 클라이언트가 null이나 빈문자열을 보낼 수 있으니 예외 처리
                if (groupDto.getThumbnail() == null || groupDto.getThumbnail().isEmpty()) {
                    groupDto.setThumbnail(null);  // 기본 이미지 사용을 위해 null로 세팅
                    groupDto.setThumbnailFullPath(null);
                }
                // 기존 DB 이미지 유지하고 싶으면, service단에서 기존값 유지하도록 로직 추가 필요
            }

            groupDto.setGroupId(id);
            groupService.update(groupDto);

            response.put("success", true);
            response.put("message", "스터디 그룹이 성공적으로 수정되었습니다.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("스터디 그룹 수정 실패: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("스터디 그룹 수정 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "스터디 그룹 수정 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            groupService.delete(id);

            response.put("success", true);
            response.put("message", "스터디 그룹이 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("스터디 그룹 삭제 실패: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("스터디 그룹 삭제 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "스터디 그룹 삭제 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/check-name/{groupName}")
    public ResponseEntity<Map<String, Object>> checkGroupNameDuplicate(@PathVariable String groupName) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean isDuplicate = groupService.existsByGroupName(groupName);

            response.put("success", true);
            response.put("isDuplicate", isDuplicate);
            response.put("message", isDuplicate ?
                    "이미 사용 중인 스터디 이름입니다." : "사용 가능한 스터디 이름입니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("스터디 그룹 이름 중복 확인 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "중복 확인 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }




    // ========== 사용자 참여 그룹 조회 기능 ==========

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getStudyGroupsByUserId(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<GroupDto> groups = groupService.getStudyGroupsByUserId(userId);

            response.put("success", true);
            response.put("data", groups);
            response.put("count", groups.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("사용자 참여 그룹 조회 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "참여 그룹 목록을 불러오는 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<Map<String, Object>> getActiveStudyGroupsByUserId(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("사용자 {}의 활성 그룹 조회 요청", userId);
            List<GroupDto> groups = groupService.getActiveStudyGroupsByUserId(userId);
            log.info("조회된 활성 그룹 수: {}", groups.size());

            response.put("success", true);
            response.put("data", groups);
            response.put("count", groups.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("사용자 활성 그룹 조회 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "활성 그룹 목록을 불러오는 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

}