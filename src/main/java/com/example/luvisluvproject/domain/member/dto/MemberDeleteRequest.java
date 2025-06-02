package com.example.luvisluvproject.domain.member.dto;

import lombok.Getter;

@Getter
public class MemberDeleteRequest {

	private final String password;

	public MemberDeleteRequest(String password) {
		this.password = password;
	}
}
