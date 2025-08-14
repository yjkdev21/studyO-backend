package com.ex.tjspring.user.controller;

import com.ex.tjspring.common.service.S3DirKey;
import com.ex.tjspring.common.service.S3Service;
import com.ex.tjspring.user.dto.UserUpdateRequest;
import com.ex.tjspring.user.service.UserEditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class UserEditController {

    private final UserEditService userEditService;
    private final S3Service s3Service;
    private final ObjectMapper objectMapper;

    // JSON 기반 프로필 수정
    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updateUser(@RequestBody UserUpdateRequest dto) {
        boolean result = userEditService.updateUserInfo(dto);
        Map<String, String> response = new HashMap<>();
        response.put("message", result ? "User updated successfully" : "User not found");
        return result ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    // 이미지 포함 프로필 수정
    @PutMapping(value = "/update-with-image", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> updateUserWithImage(
            @RequestPart("userDto") String userDtoJson,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImageFile
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            UserUpdateRequest dto = objectMapper.readValue(userDtoJson, UserUpdateRequest.class);

            String storedFileName = null;
            String profileImageFullPath = null;

            // 이미지가 있으면 S3 업로드
            if (profileImageFile != null && !profileImageFile.isEmpty()) {
                try {
                    storedFileName = s3Service.upload(S3DirKey.MYPROFILEIMG, profileImageFile);
                    if (storedFileName != null) {
                        profileImageFullPath = s3Service.getFileFullPath(S3DirKey.MYPROFILEIMG, storedFileName);
                    }
                } catch (Exception e) {
                    log.error("S3 업로드 실패: {}", e.getMessage());
                }
                dto.setProfileImage(storedFileName);
                dto.setProfileImageFullPath(profileImageFullPath);
            }

            boolean result = userEditService.updateUserInfoWithImage(dto);
            if (result) {
                response.put("success", true);
                response.put("message", "프로필이 성공적으로 수정되었습니다.");
                response.put("profileImageFullPath", profileImageFullPath);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            log.error("프로필 수정 오류: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "프로필 수정 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 사용자 프로필 조회
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            UserUpdateRequest user = userEditService.getUserProfile(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                user.setProfileImageFullPath(s3Service.getFileFullPath(S3DirKey.MYPROFILEIMG, user.getProfileImage()));
            }

            response.put("success", true);
            response.put("data", user);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("프로필 조회 오류: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "프로필 조회 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
