package com.ex.tjspring.common.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

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

	@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration config
	) throws Exception {
		return config.getAuthenticationManager();
	}

	// 세션 레지스트리 빈 등록 - 동시 세션 관리를 위함
	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	// HTTP 보안 설정을 담당하는 빈 등록 - 모든 HTTP 요청에 대한 보안 규칙을 정의
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// CSRF (Cross-Site Request Forgery) 공격 방지 기능 비활성화(프론트엔드/백엔드 분리된 구조에서는 불필요함)
				.csrf(csrf -> csrf.disable())

				// WebConfig의 CORS 설정 사용
				.cors(Customizer.withDefaults())

				// 세션 관리 정책 설정
				.sessionManagement(session ->
								session
										.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
										.sessionFixation().changeSessionId()
										// 동시 세션 최대 개수 - 추후 1로 수정
//										.maximumSessions(1)
										.maximumSessions(5)
										// 새 로그인 시 기존 세션 만료
//								.maxSessionsPreventsLogin(false)
										.sessionRegistry(sessionRegistry())


				)

				// URL 별 접근 권한 설정 (위에서부터 차례대로 매칭)
				.authorizeHttpRequests(auth ->
						auth
								// 인증 없이 접근 가능 (비회원)
								.requestMatchers(
										"/api/auth/check",
										"/api/auth/login",
										"/api/user/register",
										"/api/user/check-id",
										"/api/user/check-nickname"
								).permitAll()

								// 로그인된 사용자만 접근 가능
								.requestMatchers(
										"/api/auth/logout",
										"/api/user/**"
								).authenticated()

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
