package com.example.luvisluvproject.domain.auth.dto.response;

import java.time.LocalDate;

import com.example.luvisluvproject.domain.member.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class SignupResponseDto {
	private final Long id;
	private final String name;
	private final String email;
	private final LocalDate birthday;
	private final UserRole userRole;
}
