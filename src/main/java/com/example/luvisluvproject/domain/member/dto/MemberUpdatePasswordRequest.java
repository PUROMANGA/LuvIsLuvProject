package com.example.luvisluvproject.domain.member.dto;

import lombok.Getter;

@Getter
public class MemberUpdatePasswordRequest {

	private final String oldPassword;

	private final String newPassword;

	public MemberUpdatePasswordRequest(String oldPassword, String newPassword) {
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}
}
