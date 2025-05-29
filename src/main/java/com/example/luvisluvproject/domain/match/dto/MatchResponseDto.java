package com.example.luvisluvproject.domain.match.dto;

import com.example.luvisluvproject.domain.match.entity.Match;

import lombok.Getter;

@Getter

public class MatchResponseDto {
	private Long id;
	private Long senderId;
	private Long receiverId;
	private boolean isLike;

	public MatchResponseDto(Match match) {
		this.id = match.getId();
		this.senderId = match.getSenderId();
		this.receiverId = match.getReceiverId();
		this.isLike = match.isLike();
	}
}
