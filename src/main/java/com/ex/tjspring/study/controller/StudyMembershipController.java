package com.ex.tjspring.study.controller;


import com.ex.tjspring.study.dto.StudyMembershipDto;
import com.ex.tjspring.study.service.StudyMembershipService;
import com.ex.tjspring.user.model.User;
import com.ex.tjspring.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/membership")
public class StudyMembershipController {

	private final StudyMembershipService studyMembershipService;
	private final UserService userService;

	// 스터디 그룹의 멤버 목록 조회
	@GetMapping("/group/{groupId}")
	public ResponseEntity<List<StudyMembershipDto>> getGroupMembers(@PathVariable("groupId") Long groupId) {
		if (groupId == null || groupId <= 0) {
			return ResponseEntity.badRequest().build();
		}
		try {
			List<StudyMembershipDto> members = studyMembershipService.getGroupMembers(groupId);
			return ResponseEntity.ok(members);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// 스터디 내 닉네임 수정
	@PutMapping("/nickname")
	public ResponseEntity<Map<String, String>> updateNickname(
			@RequestBody Map<String, String> requestBody,
			HttpSession session) {

		// SecurityContext에서 인증 정보 가져오기
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인이 필요합니다."));
		}

		// 인증된 사용자의 userId 가져오기
		String userId = auth.getName();

		// User 정보 조회해서 id 얻기
		User user = userService.findByUserId(userId);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "유효하지 않은 사용자입니다."));
		}

		Long currentUserId = user.getId();

		// 요청 바디에서 그룹 ID 와 닉네임 가져오기
		String groupIdStr = requestBody.get("groupId");
		String nickname = requestBody.get("nickname");

		// 그룹 ID 검증
		if (groupIdStr == null || groupIdStr.trim().isEmpty()) {
			return ResponseEntity.badRequest()
					.body(Map.of("error", "그룹 ID는 필수입니다."));
		}

		Long groupId;
		try {
			groupId = Long.parseLong(groupIdStr);
			if (groupId <= 0) {
				return ResponseEntity.badRequest()
						.body(Map.of("error", "유효하지 않은 그룹 ID입니다."));
			}
		} catch (NumberFormatException e) {
			return ResponseEntity.badRequest()
					.body(Map.of("error", "그룹 ID는 숫자여야 합니다."));
		}

		// 닉네임 검증
		if (nickname == null || nickname.trim().isEmpty()) {
			return ResponseEntity.badRequest()
					.body(Map.of("error", "닉네임은 필수입니다."));
		}

		try {
			studyMembershipService.updateNickname(currentUserId, groupId, nickname);
			return ResponseEntity.ok(Map.of("message", "닉네임이 성공적으로 수정되었습니다."));

		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest()
					.body(Map.of("error", e.getMessage()));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "서버 내부 오류가 발생했습니다."));
		}
	}


}
