package com.example.luvisluvproject.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.domain.auth.dto.request.LoginRequestDto;
import com.example.luvisluvproject.domain.auth.dto.request.SignupRequestDto;
import com.example.luvisluvproject.domain.auth.dto.response.LoginResponseDto;
import com.example.luvisluvproject.domain.auth.dto.response.SignupResponseDto;
import com.example.luvisluvproject.domain.auth.service.AuthService;
import com.example.luvisluvproject.global.config.JwtUtil;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final JwtUtil jwtUtil;

	/**
	 * 일반 유저 회원가입 요청 컨트롤러
	 *
	 * @param requestDto 회원가입 요청 데이터
	 * @return 가입된 회원 정보를 담은 응답 DTO와 200 OK 상태
	 */
	@PostMapping("/signup/user")
	public ResponseEntity<ApiResponse<SignupResponseDto>> signupUser(
		@Valid @RequestBody SignupRequestDto requestDto
	) {
		SignupResponseDto responseDto = authService.signupUser(requestDto);
		ApiResponse<SignupResponseDto> apiResponse = ApiResponse.of(SuccessCode.SIGNUP_SUCCESS, responseDto);
		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * 사장(MANAGER) 회원가입 요청 컨트롤러
	 *
	 * @param requestDto 회원가입 요청 데이터
	 * @return 가입된 회원 정보를 담은 응답 DTO와 200 OK 상태
	 */
	@PostMapping("/signup/manager")
	public ResponseEntity<ApiResponse<SignupResponseDto>> signupManager(
		@Valid @RequestBody SignupRequestDto requestDto
	) {
		SignupResponseDto responseDto = authService.signupManager(requestDto);
		ApiResponse<SignupResponseDto> apiResponse = ApiResponse.of(SuccessCode.SIGNUP_SUCCESS, responseDto);
		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * 관리자(ADMIN) 회원가입 요청 컨트롤러
	 *
	 * @param requestDto 회원가입 요청 데이터
	 * @return 가입된 회원 정보를 담은 응답 DTO와 200 OK 상태
	 */
	@PostMapping("/signup/admin")
	public ResponseEntity<ApiResponse<SignupResponseDto>> signupAdmin(
		@RequestBody @Valid SignupRequestDto requestDto
	) {
		SignupResponseDto responseDto = authService.signupAdmin(requestDto);
		ApiResponse<SignupResponseDto> apiResponse = ApiResponse.of(SuccessCode.SIGNUP_SUCCESS, responseDto);
		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * 로그인 요청 컨트롤러
	 *
	 * @param requestDto 로그인 요청 데이터 (이메일, 비밀번호)
	 * @return JWT 토큰 정보가 담긴 응답 DTO와 200 OK 상태
	 */
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponseDto>> login(
		@Valid @RequestBody LoginRequestDto requestDto
	) {
		LoginResponseDto responseDto = authService.login(requestDto);
		ApiResponse<LoginResponseDto> apiResponse = ApiResponse.of(SuccessCode.LOGIN_SUCCESS, responseDto);
		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * 로그아웃 요청 컨트롤러
	 *
	 * @param request HTTP 요청 객체 (헤더에서 토큰 추출)
	 * @return 성공 메시지
	 */
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(
		HttpServletRequest request
	) {
		String accessToken = jwtUtil.resolveToken(request);
		authService.logout(accessToken);

		ApiResponse<Void> apiResponse = ApiResponse.of(SuccessCode.LOGOUT_SUCCESS, null);
		return ResponseEntity.ok(apiResponse);
	}

}
