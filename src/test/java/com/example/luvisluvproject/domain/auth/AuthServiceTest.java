package com.example.luvisluvproject.domain.auth;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.luvisluvproject.domain.auth.common.AuthServiceHelper;
import com.example.luvisluvproject.domain.auth.dto.request.LoginRequestDto;
import com.example.luvisluvproject.domain.auth.dto.request.SignupUserRequestDto;
import com.example.luvisluvproject.domain.auth.dto.response.LoginResponseDto;
import com.example.luvisluvproject.domain.auth.dto.response.SignupResponseDto;
import com.example.luvisluvproject.domain.auth.service.AuthService;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.enums.UserRole;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.common.TestFactory;
import com.example.luvisluvproject.global.config.JwtUtil;
import com.example.luvisluvproject.global.error.CustomRuntimeException;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

	@Spy
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private RedisTemplate<String, String> tokenRedisTemplate;

	@Mock
	private ValueOperations<String, String> valueOps;

	@Mock
	private AuthServiceHelper authServiceHelper;

	@Mock
	private JwtUtil jwtUtil;

	@Spy
	private TestFactory testFactory = new TestFactory(passwordEncoder);

	@InjectMocks
	private AuthService authService;

	@Test
	@DisplayName("유저 회원가입 성공")
	void signupUserSuccess() {

		SignupUserRequestDto request = new SignupUserRequestDto(
			"박회원",
			"park1@email.com",
			"Test1234!",
			LocalDate.of(2000, 1, 1),
			UserRole.USER);

		Member member = mock(Member.class);

		//given
		given(passwordEncoder.encode(anyString())).willReturn("encoded_pw"); // 비밀번호 암호화 결과

		//객체 만들어주기
		given(authServiceHelper.createMember(any(), any(), any(), any(), any())).willReturn(member);

		//when
		SignupResponseDto signupResponseDto = authService.signup(request);

		//then
		assertThat(signupResponseDto.getId()).isEqualTo(1L);
	}

	@Nested
	@DisplayName("로그인 테스트")
	class LoginCases {

		@Test
		@DisplayName("loginTest_패스워드_실패_테스트")
		void loginTest_패스워드_실패_테스트() {

			//given
			LoginRequestDto loginRequestDto = new LoginRequestDto(
				"park1@email.com",
				"Test1234!");

			Member member = testFactory.loginTestOf("name", loginRequestDto.getEmail(), "Test5678!");
			given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));

			//when
			CustomRuntimeException customRuntimeException = assertThrows(CustomRuntimeException.class,
				() -> authService.login(loginRequestDto));

			//then
			assertThat(loginRequestDto.getPassword()).isNotEqualTo("Test5678!");
			assertEquals("이메일 혹은 비밀번호가 올바르지 않습니다.", customRuntimeException.getMessage());
		}

		@Test
		@DisplayName("loginTest_패스워드_성공_테스트")
		void loginTest_패스워드_성공_테스트() {

			//given

			LoginRequestDto loginRequestDto = new LoginRequestDto(
				"park1@email.com",
				"Test1234!");

			Member member = testFactory.loginTestOf("name", loginRequestDto.getEmail(), "Test1234!");

			given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
			given(tokenRedisTemplate.opsForValue()).willReturn(valueOps);

			//when
			LoginResponseDto loginResponseDto = authService.login(loginRequestDto);

			//then
			assertThat(loginResponseDto.getUserId()).isEqualTo(member.getId());
		}
	}
}