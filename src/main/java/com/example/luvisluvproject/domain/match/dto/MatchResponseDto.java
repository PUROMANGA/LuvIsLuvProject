package com.example.luvisluvproject.domain.match.dto;

import com.example.luvisluvproject.domain.match.entity.Match;
import com.example.luvisluvproject.domain.match.entity.MatchStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class MatchResponseDto {
	private Long senderId;
	private String senderName;
	private Long receiverId;
	private String receiverName;
	private MatchStatus matchStatus;

	public MatchResponseDto(Long senderId, String senderName, Long receiverId, String receiverName) {
		this.senderId = senderId;
		this.senderName = senderName;
		this.receiverId = receiverId;
		this.receiverName = receiverName;
	}

	public MatchResponseDto(Match match, String senderName, String receiverName) {
		this.senderId = match.getSenderId();
		this.senderName = senderName;
		this.receiverId = match.getReceiverId();
		this.receiverName = receiverName;
		this.matchStatus = match.getMatchStatus();
	}
}
