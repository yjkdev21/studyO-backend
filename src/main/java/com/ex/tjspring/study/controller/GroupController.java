package com.ex.tjspring.study.controller;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/study")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"}) // CORS 설정 추가
public class GroupController {

    @Autowired
    private GroupService groupService;

    // ========== 기존 코드들 (그대로 유지) ==========

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> createGroup(
            @RequestPart("groupDto") GroupDto groupDto,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnailFile
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                log.info("썸네일 파일 수신: {}", thumbnailFile.getOriginalFilename());
                String filename = thumbnailFile.getOriginalFilename();
                groupDto.setThumbnail(filename);
            }

            groupService.insert(groupDto);

            response.put("success", true);
            response.put("message", "스터디 그룹이 성공적으로 등록되었습니다.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("스터디 그룹 생성 실패: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("스터디 그룹 생성 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "스터디 그룹 등록 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllGroups() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<GroupDto> groups = groupService.selectAllGroups();

            response.put("success", true);
            response.put("data", groups);
            response.put("count", groups.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("스터디 그룹 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "목록을 불러오는 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getGroup(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            GroupDto group = groupService.selectGroupById(id);

            response.put("success", true);
            response.put("data", group);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("스터디 그룹 조회 실패: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("스터디 그룹 조회 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "그룹 정보를 불러오는 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Long id,
            @Validated @RequestBody GroupDto groupDto,
            BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (bindingResult.hasErrors()) {
                response.put("success", false);
                response.put("message", "필수 필드를 모두 입력해주세요.");
                return ResponseEntity.badRequest().body(response);
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

    // ========== 새로 추가되는 사용자 참여 그룹 조회 기능 ==========

    // 특정 사용자가 참여한 모든 스터디 그룹 조회 (현재 + 과거)
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

    // 특정 사용자가 현재 참여 중인 스터디 그룹만 조회
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<Map<String, Object>> getActiveStudyGroupsByUserId(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("사용자 {}의 활성 그룹 조회 요청", userId); // 로그 추가
            List<GroupDto> groups = groupService.getActiveStudyGroupsByUserId(userId);
            log.info("조회된 활성 그룹 수: {}", groups.size()); // 로그 추가

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