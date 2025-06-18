package com.example.luvisluvproject.domain.match.dto;

import com.example.luvisluvproject.domain.match.entity.Match;
import com.example.luvisluvproject.domain.match.entity.MatchStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchResponseDto {
	private Long senderId;
	private String senderName;
	private Long receiverId;
	private String receiverName;
	private MatchStatus matchStatus;

	public MatchResponseDto(Match match) {
		this.senderId = match.getSenderId();
		this.receiverId = match.getReceiverId();
		this.matchStatus = match.getMatchStatus();
	}

	public MatchResponseDto(Match match, String senderName, String receiverName) {
		this.senderId = match.getSenderId();
		this.senderName = senderName;
		this.receiverId = match.getReceiverId();
		this.receiverName = receiverName;
		this.matchStatus = match.getMatchStatus();
	}
}
