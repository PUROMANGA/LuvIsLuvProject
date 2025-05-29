package com.example.luvisluvproject.domain.match.dto;

import com.example.luvisluvproject.domain.member.entity.Member;

public class MatchRequestDto {

	private Long senderId;

	MatchRequestDto(Member member) {
		this.senderId = member.getId();
	}
}
