package com.example.luvisluvproject.domain.match.dto;

import com.example.luvisluvproject.domain.match.entity.Match;
import com.example.luvisluvproject.domain.match.entity.MatchStatus;

import lombok.Getter;

@Getter

public class MatchResponseDto {
	private Long id;
	private Long senderId;
	private Long receiverId;
	private MatchStatus matchStatus;

	public MatchResponseDto(Match match) {
		this.id = match.getId();
		this.senderId = match.getSenderId();
		this.receiverId = match.getReceiverId();
		this.matchStatus = match.getMatchStatus();
	}

	public MatchResponseDto(Long id, Long senderId, Long receiverId, MatchStatus matchStatus) {
		this.id = id;
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.matchStatus = matchStatus;
	}
}
