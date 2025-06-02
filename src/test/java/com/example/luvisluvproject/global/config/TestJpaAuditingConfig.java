// package com.example.luvisluvproject.global.config;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.domain.AuditorAware;
//
// import java.util.Optional;
//
// /**
//  * 테스트 환경용 AuditorAware 설정
//  */
// @Configuration
// public class TestJpaAuditingConfig {
//
// 	@Bean
// 	public AuditorAware<String> auditorAware() {
// 		return () -> Optional.of("test-user");
// 	}
// }