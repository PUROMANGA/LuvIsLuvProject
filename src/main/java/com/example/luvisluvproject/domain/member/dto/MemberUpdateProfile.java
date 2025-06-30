package com.example.luvisluvproject.domain.member.dto;

import lombok.Getter;

@Getter
public class MemberUpdateProfile {

	private final String content;
	private final String oldPassword;
	private final String newPassword;

	public MemberUpdateProfile(String content, String oldPassword, String newPassword) {
		this.content = content;
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}
}
