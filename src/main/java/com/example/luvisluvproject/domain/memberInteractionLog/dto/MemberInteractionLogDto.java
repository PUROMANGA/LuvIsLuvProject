package com.example.luvisluvproject.domain.memberInteractionLog.dto;

import java.util.HashMap;
import java.util.Map;

import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberInteractionLog;

import lombok.Getter;

@Getter
public class MemberInteractionLogDto {

	private final Long memberId;

	//매칭을 돌린 횟수
	private final Double getMatchCount;

	//수락한 횟수
	private final Double matchReceivedCount;

	//신청한 횟수
	private final Double matchRequestCount;

	private final Double matchScore;

	//메세지를 보낸 횟수
	private final Double messageCount;

	private final Map<String, Double> tags;

	public MemberInteractionLogDto(MemberInteractionLog memberInteractionLog, Double matchScore, Map<String, Double> tags) {
		this.memberId = memberInteractionLog.getMemberId();
		this.getMatchCount = memberInteractionLog.getGetMatchCount();
		this.matchReceivedCount = memberInteractionLog.getMatchReceivedCount();
		this.matchRequestCount = memberInteractionLog.getMatchRequestCount();
		this.messageCount = memberInteractionLog.getMessageCount();
		this.matchScore = matchScore;
		this.tags = tags;
	}
}
