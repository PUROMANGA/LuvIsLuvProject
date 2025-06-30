package com.example.luvisluvproject.domain.auth.dto.response;

import java.time.LocalDate;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class SignupResponseDto {
	private final Long id;
	private final String name;
	private final String email;
	private final LocalDate birthday;
	private final UserRole userRole;

	public SignupResponseDto(Member member) {
		this.id = member.getId();
		this.name = member.getName();
		this.email = member.getEmail();
		this.birthday = member.getBirthday();
		this.userRole = member.getUserRole();
	}
}
