package com.example.luvisluvproject.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.domain.auth.dto.request.SignupRequestDto;
import com.example.luvisluvproject.domain.auth.dto.response.SignupResponseDto;
import com.example.luvisluvproject.domain.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<SignupResponseDto> signup(@Validated @RequestBody SignupRequestDto requestDto) {
		return ResponseEntity.ok(authService.signup(requestDto));
	}

}
