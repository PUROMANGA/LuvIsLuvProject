package com.example.luvisluvproject.domain.member.dto;

import lombok.Getter;

@Getter
public class MemberUpdateRequest {

	private final String oldPassword;

	private final String newPassword;

	public MemberUpdateRequest(String oldPassword, String newPassword) {
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}
}
