package com.example.luvisluvproject.domain.auth.dto.response;

import java.time.LocalDate;

import com.example.luvisluvproject.domain.member.enums.UserRole;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignupUserResponseDto {

	private final Long id;
	private final String name;
	private final String email;
	private final LocalDate birthday;
	private final UserRole userRole;
	private final String content;

}
