package com.example.luvisluvproject.domain.member.dto;

import lombok.Getter;

@Getter
public class MemberPasswordRequest {

	private final String oldPassword;

	public MemberPasswordRequest(String oldPassword) {
		this.oldPassword = oldPassword;
	}
}
