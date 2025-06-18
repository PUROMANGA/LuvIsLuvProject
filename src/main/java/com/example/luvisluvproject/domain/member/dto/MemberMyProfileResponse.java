package com.example.luvisluvproject.domain.member.dto;

import java.time.LocalDate;
import java.time.Period;

import lombok.Getter;

@Getter
public class MemberMyProfileResponse {

	private final Long userId;

	private final String name;

	private final String email;

	private final LocalDate birthday;

	private final int age;

	private final String content;

	public MemberMyProfileResponse(Long userId, String name, String email, LocalDate birthday, String content) {
		this.userId = userId;
		this.name = name;
		this.email = email;
		this.birthday = birthday;
		this.age = calculateAge(birthday);
		this.content = content;
	}

	private int calculateAge(LocalDate birthday) {
		return Period.between(birthday, LocalDate.now()).getYears();
	}
}
