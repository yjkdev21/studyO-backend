package com.ex.tjspring.auth.controller;

import com.ex.tjspring.auth.dto.LoginRequestDto;
import com.ex.tjspring.auth.dto.LoginResponseDto;
import com.ex.tjspring.user.model.User;
import com.ex.tjspring.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthenticationManager authenticationManager;  // Spring Security 인증 매니저
	private final UserService userService;

	// 로그인 - Spring Security + 명시적 세션 관리
	@PostMapping("/login")
	public ResponseEntity<?> login(
			@Valid @RequestBody LoginRequestDto loginRequest,
			HttpServletRequest request
	) {
		try {
			// Spring Security로 인증 처리
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							loginRequest.getUserId(),
							loginRequest.getPassword()
					)
			);

			// 인증 성공 시 SecurityContext에 저장
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// 명시적으로 세션 생성 및 저장
			HttpSession session = request.getSession(true); // true = 세션이 없으면 새로 생성
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

			// 사용자 정보 조회 (응답용)
			User user = userService.findByUserId(loginRequest.getUserId());

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

		} catch (AuthenticationException e) {
			log.warn("로그인 실패 : 잘못된 인증 정보 - userId={}", loginRequest.getUserId());
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of(
							"success", false,
							"message", "아이디 또는 비밀번호가 잘못되었습니다."
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

	// 로그아웃 - Spring Security 방식으로 변경
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request) {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				String userId = auth.getName();
				log.info("로그아웃 처리: userId={}", userId);
			}

			// Spring Security의 SecurityContext 클리어
			SecurityContextHolder.clearContext();

			// 세션 무효화
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.invalidate();
			}

			return ResponseEntity.ok(Map.of(
					"success", true,
					"message", "로그아웃되었습니다."
			));

		} catch (Exception e) {
			log.error("로그아웃 처리 중 예외 발생", e);
			return ResponseEntity.ok(Map.of(
					"success", true,
					"message", "로그아웃되었습니다."
			));
		}
	}

	// 로그인 체크 - Spring Security 방식으로 변경
	@GetMapping("/check")
	public ResponseEntity<?> checkLoginStatus() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			// 인증되지 않았거나 익명 사용자인 경우
			if (authentication == null || !authentication.isAuthenticated()
					|| "anonymousUser".equals(authentication.getName())) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of(
								"success", false,
								"isLoggedIn", false,
								"message", "로그인이 필요합니다."
						));
			}

			String userId = authentication.getName();
			log.info("인증된 사용자: {}", userId);

			// 최신 사용자 정보 조회
			User user = userService.findByUserId(userId);
			if (user == null) {
				SecurityContextHolder.clearContext(); // 유효하지 않은 인증 정보 제거
				log.warn("유효하지 않은 사용자 인증 정보 제거: userId={}", userId);

				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of(
								"success", false,
								"isLoggedIn", false,
								"message", "유효하지 않은 인증 정보입니다."
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