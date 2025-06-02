package com.example.luvisluvproject.domain.member.dto;

import lombok.Getter;

@Getter
public class MemberFindResponse {

	private final Long userId;

	private final String name;

	private final String email;

	private final String birthday;

	public MemberFindResponse(Long userId, String name, String email, String birthday) {
		this.userId = userId;
		this.name = name;
		this.email = email;
		this.birthday = birthday;
	}
}
