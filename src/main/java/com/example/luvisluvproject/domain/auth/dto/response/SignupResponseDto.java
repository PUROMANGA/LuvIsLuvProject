package com.example.luvisluvproject.domain.auth.dto.response;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.enums.UserRole;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignupResponseDto {

	private final Long id;
	private final String name;
	private final String email;
	private final String birthday;
	private final UserRole userRole;


}
