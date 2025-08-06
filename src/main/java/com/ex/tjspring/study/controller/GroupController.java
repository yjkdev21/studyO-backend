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
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                String originalFilename = thumbnailFile.getOriginalFilename();
                String fileName = UUID.randomUUID() + "_" + originalFilename;

                // 파일 데이터를 byte[]로 변환
                byte[] imageData = thumbnailFile.getBytes();

                log.info("파일 업로드: {} -> {} ({}bytes)", originalFilename, fileName, imageData.length);

                // GroupDto에 파일명과 이미지 데이터 설정
                groupDto.setThumbnail(fileName);
                groupDto.setImageData(imageData); // GroupDto에 imageData 필드 추가 필요

            } else {
                log.info("썸네일 파일이 없음, 기본 이미지 사용");
                groupDto.setThumbnail(null); // null로 설정하여 기본 이미지 사용
                groupDto.setImageData(null);
            }

            groupService.insertWithMembership(groupDto);

            response.put("success", true);
            response.put("message", "스터디 그룹이 성공적으로 등록되었습니다.");
            response.put("thumbnailFileName", groupDto.getThumbnail());
            return ResponseEntity.ok(response);

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

            // 디버깅용 로그
            for (GroupDto group : groups) {
                log.debug("그룹 {}: 썸네일 = {}", group.getGroupName(), group.getThumbnail());
            }

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

            log.info("그룹 조회 - ID: {}, 썸네일: {}", id, group.getThumbnail());

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

//    @GetMapping("/image/{filename}")
//    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
//        byte[] imageData = groupDao.selectImageByFilename(filename);
//
//        if (imageData == null || imageData.length == 0) {
//            return ResponseEntity.notFound().build();
//        }
//
//        String contentType = getContentType(filename);
//        ByteArrayResource resource = new ByteArrayResource(imageData);
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
//                .body(resource);
//    }
//
//    private String getContentType(String filename) {
//        if (filename == null || !filename.contains(".")) {
//            return "image/default-thumbnail.png";
//        }
//
//        String extension = filename.toLowerCase().substring(filename.lastIndexOf(".") + 1);
//
//        switch (extension) {
//            case "jpg":
//            case "jpeg":
//                return "image/jpeg";
//            case "png":
//                return "image/png";
//            case "gif":
//                return "image/gif";
//            case "webp":
//                return "image/webp";
//            default:
//                return "image/jpeg";
//        }
//    }




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