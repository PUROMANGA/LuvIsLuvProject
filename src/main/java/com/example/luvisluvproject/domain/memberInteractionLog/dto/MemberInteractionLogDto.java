package com.example.luvisluvproject.domain.memberInteractionLog.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberInteractionLog;
import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberTagLikeCount;

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

	private final List<MemberTagLikeCount> tags;

	private final LocalDateTime creatTime;

	public MemberInteractionLogDto(MemberInteractionLog memberInteractionLog, Double matchScore, List<MemberTagLikeCount> tags) {
		this.memberId = memberInteractionLog.getMemberId();
		this.getMatchCount = memberInteractionLog.getGetMatchCount();
		this.matchReceivedCount = memberInteractionLog.getMatchReceivedCount();
		this.matchRequestCount = memberInteractionLog.getMatchRequestCount();
		this.messageCount = memberInteractionLog.getMessageCount();
		this.matchScore = matchScore;
		this.tags = tags;
		this.creatTime = memberInteractionLog.getCreatTime();
	}
}
