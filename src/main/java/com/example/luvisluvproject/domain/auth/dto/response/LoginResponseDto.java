package com.example.luvisluvproject.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
	private final String accessToken;
	private final String refreshToken;
}
