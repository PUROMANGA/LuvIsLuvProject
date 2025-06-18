package com.example.luvisluvproject.domain.auth;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.luvisluvproject.domain.auth.common.AuthServiceHelper;
import com.example.luvisluvproject.domain.auth.dto.request.SignupUserRequestDto;
import com.example.luvisluvproject.domain.auth.dto.response.SignupResponseDto;
import com.example.luvisluvproject.domain.auth.service.AuthService;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.enums.UserRole;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.config.JwtUtil;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private RedisTemplate<String, String> tokenRedisTemplate;

	@Mock
	private ValueOperations<String, String> valueOps;

	@Mock
	private AuthServiceHelper authServiceHelper;

	@InjectMocks
	private AuthService authService;

	@Test
	@DisplayName("유저 회원가입 성공")
	void signupUserSuccess() {

		SignupUserRequestDto request  = new SignupUserRequestDto(
			"박회원",
			"park1@email.com",
			"Test1234!",
			LocalDate.of(2000, 1, 1),
			"USER");

		Member member = new Member(
			1L,
			"박회원",
			"park1@email.com",
			"Test1234!",
			LocalDate.of(2000, 1, 1),
			UserRole.USER);

		//given
		given(passwordEncoder.encode(anyString())).willReturn("encoded_pw"); // 비밀번호 암호화 결과
		//객체 만들어주기
		given(authServiceHelper.createMember(any(), any(), any(), any(), any())).willReturn(member);

		//when
		SignupResponseDto signupResponseDto = authService.signup(request);

		assertThat(signupResponseDto.getId()).isEqualTo(1L);
	}
}