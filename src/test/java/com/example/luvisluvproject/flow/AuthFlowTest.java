package com.example.luvisluvproject.flow;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.luvisluvproject.domain.auth.dto.request.LoginRequestDto;
import com.example.luvisluvproject.domain.auth.dto.request.SignupUserRequestDto;
import com.example.luvisluvproject.domain.auth.dto.response.LoginResponseDto;
import com.example.luvisluvproject.domain.auth.dto.response.SignupResponseDto;
import com.example.luvisluvproject.domain.auth.service.AuthService;
import com.example.luvisluvproject.domain.member.enums.UserRole;
import com.example.luvisluvproject.global.config.JwtUtil;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class AuthFlowTest {

	@Autowired
	private AuthService authService;

	@Autowired
	private JwtUtil jwtUtil;

	@Test
	@DisplayName("회원가입 테스트")
	void 회원가입_테스트() {
		SignupUserRequestDto signupUserRequestDto = new SignupUserRequestDto(
			"강호동",
			"test1234@email.com",
			"testpw1234",
			LocalDate.of(2000, 1, 1),
			UserRole.USER
		);
		SignupResponseDto signupResponseDto = authService.signup(signupUserRequestDto);
		System.out.println("signupResponseDto = " + signupResponseDto);
		ApiResponse<SignupResponseDto> apiResponse = ApiResponse.of(SuccessCode.SIGNUP_SUCCESS, signupResponseDto);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("회원가입 완료");
	}

	@Test
	@DisplayName("로그인 테스트")
	void 로그인_테스트() {
		LoginRequestDto loginRequestDto = new LoginRequestDto(
			"test1234@email.com",
			"testpw1234"
		);
		LoginResponseDto loginResponseDto = authService.login(loginRequestDto);
		System.out.println("loginResponseDto = " + loginResponseDto);
		ApiResponse<LoginResponseDto> apiResponse = ApiResponse.of(SuccessCode.LOGIN_SUCCESS, loginResponseDto);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("로그인 성공");
	}
}
