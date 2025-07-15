package com.example.luvisluvproject.flow;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockHttpServletResponse;

import com.example.luvisluvproject.domain.auth.dto.request.LoginRequestDto;
import com.example.luvisluvproject.domain.auth.dto.response.LoginResponseDto;
import com.example.luvisluvproject.domain.auth.service.AuthService;
import com.example.luvisluvproject.global.common.TestFactory;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.config.JwtFilter;
import com.example.luvisluvproject.global.config.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class LogoutTest {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	@Qualifier("customStringRedisTemplate")
	private RedisTemplate<String, String> tokenRedisTemplate;

	@Autowired
	private JwtFilter jwtFilter;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private TestFactory testFactory;

	@Autowired
	private AuthService authService;

	private LoginResponseDto loginResponseDto;

	private Member member;

	@BeforeEach
	void setUp() {
		member = testFactory.loginTestOf("name", "test@naver.com", "test1234");
		memberRepository.save(member);
		LoginRequestDto loginRequestDto = new LoginRequestDto(
			member.getEmail(), "test1234");
		loginResponseDto = authService.login(loginRequestDto);
		authService.logout(loginResponseDto.getAccessToken().substring(7), "test@naver.com");

	}

	// @AfterEach
	// void cleanup() {
	// 	memberRepository.deleteAll();
	// 	tokenRedisTemplate.delete(loginResponseDto.getAccessToken().substring(7));
	// }

	@Nested
	@DisplayName("토큰 테스트")
	class TokenCases {
		@Test
		@DisplayName("로그아웃 테스트")
		void 로그아웃_토큰_테스트() throws IOException {
			MockHttpServletResponse response = new MockHttpServletResponse();

			if(loginResponseDto.getAccessToken() != null) {
				String isLogout = tokenRedisTemplate.opsForValue().get(loginResponseDto.getAccessToken().substring(7));

				if("logout".equals(isLogout)) {
					log.info("로그아웃된 토큰으로 접근 시도");
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그아웃된 토큰입니다.");
				}
			}
			assertThat(response.getErrorMessage()).isEqualTo("로그아웃된 토큰입니다.");
		}

		@Test
		@DisplayName("토큰 재발급 테스트")
		void 토큰_재발급_테스트() throws InterruptedException {
			String accessToken = loginResponseDto.getAccessToken().substring(7);
			String refreshToken = loginResponseDto.getRefreshToken().substring(7);
			Thread.sleep(1100);
			String newAccessToken = authService.refreshService(refreshToken, "test@naver.com");
			assertThat(newAccessToken.substring(7)).isNotEqualTo(accessToken);
		}
	}
}
