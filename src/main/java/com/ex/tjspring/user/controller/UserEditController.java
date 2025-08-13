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

    // 기존 JSON 방식 유지 (하위 호환성)
    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updateUser(@RequestBody UserUpdateRequest dto) {
        log.info("JSON 방식 프로필 수정 요청 - ID: {}, 닉네임: {}", dto.getId(), dto.getNickname());

        boolean result = userEditService.updateUserInfo(dto);
        Map<String, String> response = new HashMap<>();

        if (result) {
            response.put("message", "User updated successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "User not found");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 새로운 이미지 업로드용 엔드포인트 (기존 코드에 영향 없음)
    @PutMapping(value = "/update-with-image", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> updateUserWithImage(
            @RequestPart("userDto") String userDtoJson,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImageFile
    ) {
        log.info("이미지 포함 프로필 수정 요청 시작");
        log.info("받은 JSON: {}", userDtoJson);
        log.info("받은 파일: {}", profileImageFile != null ? profileImageFile.getOriginalFilename() : "없음");

        Map<String, Object> response = new HashMap<>();

        try {
            // JSON 문자열을 UserUpdateRequest 객체로 변환
            UserUpdateRequest dto = objectMapper.readValue(userDtoJson, UserUpdateRequest.class);

            log.info("파싱된 DTO - ID: {}, 닉네임: {}", dto.getId(), dto.getNickname());

            String storedFileName = null;
            String profileImageFullPath = null;

            // 프로필 이미지가 있으면 S3에 업로드
            if (profileImageFile != null && !profileImageFile.isEmpty()) {
                log.info("이미지 파일 업로드 시작: {}", profileImageFile.getOriginalFilename());
                try {
                    storedFileName = s3Service.upload(S3DirKey.MYPROFILEIMG, profileImageFile);
                    if (storedFileName != null) {
                        profileImageFullPath = s3Service.getFileFullPath(S3DirKey.MYPROFILEIMG, storedFileName);
                        log.info("이미지 업로드 완료 - 파일명: {}, URL: {}", storedFileName, profileImageFullPath);
                    }
                } catch (Exception e) {
                    log.error("S3 업로드 실패: {}", e.getMessage());
                    // S3 업로드 실패 시에도 다른 정보는 업데이트하도록 계속 진행
                }

                // DTO에 파일 정보 설정
                dto.setProfileImage(storedFileName);
                dto.setProfileImageFullPath(profileImageFullPath);
            }

            boolean result = userEditService.updateUserInfoWithImage(dto);

            if (result) {
                response.put("success", true);
                response.put("message", "프로필이 성공적으로 수정되었습니다.");
                response.put("profileImageFullPath", profileImageFullPath);
                log.info("프로필 수정 성공 - ID: {}", dto.getId());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            log.error("프로필 수정 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "프로필 수정 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 사용자 프로필 조회 (프로필 이미지 URL 포함)
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable Long userId) {
        log.info("사용자 프로필 조회 요청 - ID: {}", userId);

        Map<String, Object> response = new HashMap<>();

        try {
            UserUpdateRequest user = userEditService.getUserProfile(userId);

            if (user == null) {
                response.put("success", false);
                response.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }

            // S3 URL 생성 (값이 있을 때만) - 기본 이미지 처리는 프론트엔드에서 담당
            if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                user.setProfileImageFullPath(s3Service.getFileFullPath(S3DirKey.MYPROFILEIMG, user.getProfileImage()));
            }

            response.put("success", true);
            response.put("data", user);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("사용자 프로필 조회 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "프로필 조회 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}