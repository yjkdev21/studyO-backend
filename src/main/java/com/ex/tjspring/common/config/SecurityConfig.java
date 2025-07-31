package com.ex.tjspring.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 보안 설정 클래스
 * - 비밀번호 암호화 방식 설정
 * - URL별 접근 권한 관리
 * - 인증 방식 설정 (세션 기반)
 */
@Configuration      // 스프링 설정 클래스임을 선언
@EnableWebSecurity  // Spring Sequrity 활성화
public class SecurityConfig {

	// 비밀번호 암호화를 위한 BCrypt 빈 등록
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();     // BCrypt 방식으로 비밀번호 암호화
	}

	// HTTP 보안 설정을 담당하는 빈 등록 - 모든 HTTP 요청에 대한 보안 규칙을 정의
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// CSRF (Cross-Site Request Forgery) 공격 방지 기능 비활성화
				// - 프론트엔드/백엔드 분리된 구조에서는 불필요
				.csrf(csrf -> csrf.disable())

				// 세션 관리 정책 설정 - 로그인 성공 시에만 세션 생성
				.sessionManagement(session ->
						session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

				// URL 별 접근 권한 설정 (위에서부터 차례대로 매칭)
				.authorizeHttpRequests(auth ->
						auth
								// 인증 없이 접근 가능 (비회원)
								.requestMatchers(
										"/api/auth/login",
										"/api/user/register",
										"/api/user/check-id",
										"/api/user/check-nickname"
								).permitAll()
								// 추후 변경 예정
								.requestMatchers("/api/auth/check", "/api/auth/logout").permitAll()

								// 로그인된 사용자만 접근 가능
								.requestMatchers("/api/user/**").authenticated()

								// 그외 모든 요청은 허용
								.anyRequest().permitAll()
				)

				// Spring Security에서 제공하는 폼 로그인 기능 비활성화 (REST API로 처리)
				.formLogin(form -> form.disable())
				// HTTP Basic 인증 비활성화 (REST API로 처리)
				.httpBasic(basic -> basic.disable());

		return http.build();
	}
}
