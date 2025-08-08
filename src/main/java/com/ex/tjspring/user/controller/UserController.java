package com.ex.tjspring.user.controller;

import com.ex.tjspring.user.dto.UserRegisterRequest;
import com.ex.tjspring.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/register")
	public String register(@RequestBody UserRegisterRequest request) {
		return userService.registerUser(request);
	}

	//  ID 중복 확인
	@GetMapping("/check-id")
	public String checkUserId(@RequestParam String userId) {
		return userService.isUserIdExists(userId) ? "exists" : "available";
	}

	//  닉네임 중복 확인
	@GetMapping("/check-nickname")
	public String checkNickname(@RequestParam String nickname) {
		return userService.isNicknameExists(nickname) ? "exists" : "available";
	}

	// 회원 탈퇴
	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteAccount(HttpServletRequest request) {

		try {

			// 현재 로그인된 사용자 정보 가져오기
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of(
								"success", false,
								"message", "로그인이 필요합니다."
						));
			}

			String userId = auth.getName();

			// 회원 탈퇴 처리
			boolean isDeleted = userService.deleteUser(userId);

			if (!isDeleted) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of(
								"success", false,
								"message", "회원 탈퇴 처리에 실패했습니다."
						));
			}

			// 탈퇴 후 로그아웃 처리
			SecurityContextHolder.clearContext();
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.invalidate();
			}

			log.info("회원 탈퇴 완료: userId={}", userId);

			return ResponseEntity.ok(Map.of(
					"success", true,
					"message", "회원 탈퇴가 완료되었습니다."
			));
		} catch (Exception e) {
			log.error("회원 탈퇴 처리 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of(
							"success", false,
							"message", "회원 탈퇴 처리 중 오류가 발생했습니다."
					));
		}
	}

    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String password = request.get("password");

            if (userId == null || password == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "사용자 ID와 비밀번호를 입력해주세요."
                        ));
            }

            boolean isValid = userService.verifyPassword(userId, password);

            if (isValid) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "비밀번호가 확인되었습니다."
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "비밀번호가 일치하지 않습니다."
                ));
            }

        } catch (Exception e) {
            log.error("비밀번호 확인 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "비밀번호 확인 중 오류가 발생했습니다."
                    ));
        }
    }
}
