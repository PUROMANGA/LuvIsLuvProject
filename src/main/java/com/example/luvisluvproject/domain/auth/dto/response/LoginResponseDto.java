package com.example.luvisluvproject.domain.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginResponseDto {

	private final String accessToken;
	private final String refreshToken;

}
