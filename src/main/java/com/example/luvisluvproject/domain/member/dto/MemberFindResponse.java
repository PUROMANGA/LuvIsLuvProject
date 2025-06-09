package com.example.luvisluvproject.domain.member.dto;

import java.time.LocalDate;

import com.example.luvisluvproject.domain.member.entity.Member;

import lombok.Getter;

@Getter
public class MemberFindResponse {

	private final Long userId;

	private final String name;

	private final String email;

	private final LocalDate birthday;


	private Member member;

	public MemberFindResponse(Long userId, String name, String email, LocalDate birthday) {
		this.userId = userId;
		this.name = name;
		this.email = email;
		this.birthday = birthday;
	}
}
