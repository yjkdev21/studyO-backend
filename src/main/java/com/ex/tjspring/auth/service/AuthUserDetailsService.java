package com.ex.tjspring.auth.service;

import com.ex.tjspring.user.model.User;
import com.ex.tjspring.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security 에서 사용자 인증 정보를 제공하는 서비스 - UserService를 활용해 사용자 정보 조회
@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {
	private final UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// UserService로 사용자 정보 조회
		User user = userService.findByUserId(username);

		if (user == null) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);

		}

		// 사용자 권한 설정 - GLOBAL_ROLE 컬럼 값 사용
		String role = user.getGlobalRole();
		String authority = "ROLE_" + (role != null ? role : "USER");

		// UserDetails 객체 생성 및 반환
		return org.springframework.security.core.userdetails.User.builder()
				.username(user.getUserId())           // 사용자 ID
				.password(user.getPassword())         // DB에서 조회한 암호화된 비밀번호
				.authorities(authority)               // 단일 권한 (ROLE_ADMIN 또는 ROLE_USER)
				.accountExpired(false)                // 계정 만료 여부
				.accountLocked(false)                 // 계정 잠금 여부
				.credentialsExpired(false)            // 자격증명 만료 여부
				.disabled(false)                      // 계정 비활성화 여부
				.build();


	}
}
