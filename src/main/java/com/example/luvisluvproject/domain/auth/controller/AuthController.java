package com.example.luvisluvproject.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.domain.auth.dto.request.LoginRequestDto;
import com.example.luvisluvproject.domain.auth.dto.request.SignupUserRequestDto;
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
	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<SignupResponseDto>> signupUser(
		@Valid @RequestBody SignupUserRequestDto requestDto) {
		SignupResponseDto responseDto = authService.signup(requestDto);
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
		@Valid @RequestBody LoginRequestDto requestDto) {
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
		HttpServletRequest request) {
		String accessToken = jwtUtil.resolveToken(request);
		authService.logout(accessToken);
		ApiResponse<Void> apiResponse = ApiResponse.of(SuccessCode.LOGOUT_SUCCESS, null);
		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * 가지고 있는 리프레시 토큰으로 엑세스 토큰을 새로 발급해줍니다.
	 * @param request
	 * @return
	 */
	@GetMapping("/refresh")
	public ResponseEntity<ApiResponse<String>> refresh(HttpServletRequest request) {
		String refreshToken = jwtUtil.resolveToken(request);
		String accessToken = authService.refreshService(refreshToken);
		ApiResponse<String> apiResponse = ApiResponse.of(SuccessCode.REFRESH_TOKEN, accessToken);
		return ResponseEntity.ok(apiResponse);
	}
}
