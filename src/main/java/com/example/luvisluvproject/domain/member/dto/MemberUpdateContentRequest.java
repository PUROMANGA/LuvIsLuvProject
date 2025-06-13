package com.example.luvisluvproject.domain.member.dto;

import lombok.Getter;

@Getter
public class MemberUpdateContentRequest {

	private final String content;

	public MemberUpdateContentRequest(String content) {
		this.content = content;
	}
}
