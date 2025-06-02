// package com.example.luvisluvproject.global.config;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.data.domain.AuditorAware;
//
// import java.util.Optional;
//
// /**
//  * 테스트 환경에서 JPA Auditing 무력화를 위한 설정
//  */
//
// public class TestJpaConfig {
//
// 	@Bean
// 	public AuditorAware<String> auditorProvider() {
// 		// 테스트 시 인증 정보 없이도 JPA Auditing 필드 처리 가능
// 		return () -> Optional.of("test-user");
// 	}
// }
