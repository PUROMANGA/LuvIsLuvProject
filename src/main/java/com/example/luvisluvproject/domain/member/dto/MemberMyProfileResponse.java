package com.example.luvisluvproject.domain.member.dto;

import java.time.LocalDate;
import java.time.Period;

import com.example.luvisluvproject.domain.member.entity.Member;

import lombok.Getter;

@Getter
public class MemberMyProfileResponse {

	private final Long userId;

	private final String name;

	private final String email;

	private final LocalDate birthday;

	private final int age;

	private final String content;

	public MemberMyProfileResponse(Member member) {
		this.userId = member.getId();
		this.name = member.getName();
		this.email = member.getEmail();
		this.birthday = member.getBirthday();
		this.age = calculateAge(birthday);
		this.content = member.getContent();
	}

	private int calculateAge(LocalDate birthday) {
		return Period.between(birthday, LocalDate.now()).getYears();
	}
}
