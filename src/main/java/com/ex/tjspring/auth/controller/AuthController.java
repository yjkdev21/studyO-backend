package com.ex.tjspring.auth.controller;

import com.ex.tjspring.auth.dto.LoginRequestDto;
import com.ex.tjspring.auth.dto.LoginResponseDto;
import com.ex.tjspring.auth.service.AuthService;
import com.ex.tjspring.user.model.User;
import com.ex.tjspring.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private static final String LOGIN_USER_KEY = "LOGIN_USER";
    private static final int SESSION_TIMEOUT = 60 * 10; // 10분

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequestDto loginRequest,
            HttpSession session
    ) {
        try {
            User user = authService.login(loginRequest.getUserId(), loginRequest.getPassword());

            // 아이디, 비밀번호 오류
            if (user == null) {
                log.warn("로그인 실패 : 잘못된 인증 정보 - userId={}", loginRequest.getUserId());
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "success", false,
                                "message", "아이디 또는 비밀번호가 잘못되었습니다."
                        ));
            }

            // 세션에 사용자 ID 저장
            session.setAttribute(LOGIN_USER_KEY, user.getUserId());
            session.setMaxInactiveInterval(SESSION_TIMEOUT);

            log.info("로그인 성공: userId={}, sessionId={}", user.getUserId(), session.getId());

            // 성공 응답
            LoginResponseDto responseDto = new LoginResponseDto(
                    user.getId(),
                    user.getUserId(),
                    user.getEmail(),
                    user.getNickname(),
                    user.getProfileImage(),
                    user.getIntroduction(),
                    user.getCreatedAt()
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "로그인 성공",
                    "user", responseDto
            ));

        } catch (Exception e) {
            log.error("로그인 처리 중 예외 발생 - userId={}", loginRequest.getUserId(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "로그인 처리 중 오류가 발생했습니다."
                    ));
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        try {
            String userId = (String) session.getAttribute(LOGIN_USER_KEY);

            if (userId != null) {
                log.info("로그아웃 처리: userId={}, sessionId={}", userId, session.getId());
            }

            session.invalidate();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "로그아웃되었습니다."
            ));

        } catch (Exception e) {
            log.error("로그아웃 처리 중 예외 발생", e);
            // 로그아웃은 항상 성공으로 처리 (세션이 이미 무효화되어도 문제없음)
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "로그아웃되었습니다."
            ));
        }
    }

    // 로그인 체크
    @GetMapping("/check")
    public ResponseEntity<?> checkLoginStatus(HttpSession session) {
        try {
            log.info("세션 ID: {}", session.getId()); // 추가
            String userId = (String) session.getAttribute(LOGIN_USER_KEY);
            log.info("세션에서 가져온 userId: {}", userId); // 추가

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "success", false,
                                "isLoggedIn", false,
                                "message", "로그인이 필요합니다."
                        ));
            }

            // 세션에서 userId로 최신 사용자 정보 조회
            User user = userService.findByUserId(userId);
            if (user == null) {
                session.invalidate(); // 유효하지 않은 세션 제거
                log.warn("유효하지 않은 사용자 세션 무효화: userId={}", userId);

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "success", false,
                                "isLoggedIn", false,
                                "message", "유효하지 않은 세션입니다."
                        ));
            }

            LoginResponseDto responseDto = new LoginResponseDto(
                    user.getId(),
                    user.getUserId(),
                    user.getEmail(),
                    user.getNickname(),
                    user.getProfileImage(),
                    user.getIntroduction(),
                    user.getCreatedAt()
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "isLoggedIn", true,
                    "message", "로그인 상태입니다.",
                    "user", responseDto
            ));

        } catch (Exception e) {
            log.error("로그인 상태 확인 중 예외 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "isLoggedIn", false,
                            "message", "상태 확인 중 오류가 발생했습니다."
                    ));
        }
    }
}