package com.example.luvisluvproject.domain.match.dto;

import com.example.luvisluvproject.domain.match.entity.Match;
import com.example.luvisluvproject.domain.match.entity.MatchStatus;
import com.example.luvisluvproject.domain.member.entity.Member;

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

	public MatchResponseDto(Member member, Member receiverMember, Match match) {
		this.senderId = member.getId();
		this.senderName = member.getName();
		this.receiverId = receiverMember.getId();
		this.receiverName = receiverMember.getName();
		this.matchStatus = match.getMatchStatus();
	}
}
