package com.example.luvisluvproject.domain.match.dto;

import com.example.luvisluvproject.domain.member.entity.Member;

import lombok.Getter;

@Getter
public class MatchRequestDto {

	private Long receiverId;

	public MatchRequestDto(Member member) {
		this.receiverId = member.getId();
	}
}
