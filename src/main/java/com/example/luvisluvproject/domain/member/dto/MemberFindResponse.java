package com.example.luvisluvproject.domain.member.dto;

import java.time.LocalDate;
import java.time.Period;

import com.example.luvisluvproject.domain.member.entity.Member;

import lombok.Getter;

@Getter
public class MemberFindResponse {

	private final Long userId;

	private final String name;

	private final int age;

	private final String content;

	public MemberFindResponse(Long userId, String name, LocalDate birthday, String content) {
		this.userId = userId;
		this.name = name;
		this.age = calculateAge(birthday);
		this.content = content;
	}

	public MemberFindResponse(Member member) {
		this.userId = member.getId();
		this.name = member.getName();
		this.age = calculateAge(member.getBirthday());
		this.content = member.getContent();
	}

	private int calculateAge(LocalDate birthday) {
		return Period.between(birthday, LocalDate.now()).getYears();
	}
}
