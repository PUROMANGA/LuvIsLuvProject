package com.example.luvisluvproject.domain.auth.dto.response;

import lombok.Getter;

@Getter
public class LoginResponseDto {
	private final Long userId;
	private final String accessToken;
	private final String refreshToken;

	public LoginResponseDto(Long userId, String accessToken, String refreshToken) {
		this.userId = userId;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
}
