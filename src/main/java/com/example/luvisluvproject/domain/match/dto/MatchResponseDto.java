package com.example.luvisluvproject.domain.match.dto;

import com.example.luvisluvproject.domain.match.entity.Match;
import com.example.luvisluvproject.domain.match.entity.MatchStatus;

import lombok.Getter;

@Getter

public class MatchResponseDto {
	private Long id;
	private Long senderId;
	private Long receiverId;
	private boolean isLike;
	private MatchStatus matchStatus;

	public MatchResponseDto(Match match) {
		this.id = match.getId();
		this.senderId = match.getSenderId();
		this.receiverId = match.getReceiverId();
		this.isLike = match.isLike();
	}

	public MatchResponseDto(Long id, Long senderId, Long receiverId, boolean isLike, MatchStatus matchStatus) {
		this.id = id;
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.isLike = isLike;
		this.matchStatus = matchStatus;
	}
}
