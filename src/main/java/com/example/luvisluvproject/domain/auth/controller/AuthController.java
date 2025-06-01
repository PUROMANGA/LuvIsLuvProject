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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	/**
	 * 회원가입 요청 컨트롤러
	 *
	 * @param requestDto 회원가입 요청 데이터
	 * @return 가입된 회원 정보를 담은 응답 DTO와 200 OK 상태
	 */
	@PostMapping("/signup")
	public ResponseEntity<SignupResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto) {
		return ResponseEntity.ok(authService.signup(requestDto));
	}

	/**
	 * 로그인 요청 컨트롤러
	 *
	 * @param requestDto 로그인 요청 데이터 (이메일, 비밀번호)
	 * @return JWT 토큰 정보가 담긴 응답 DTO와 200 OK 상태
	 */
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
		return ResponseEntity.ok(authService.login(requestDto));
	}

}
