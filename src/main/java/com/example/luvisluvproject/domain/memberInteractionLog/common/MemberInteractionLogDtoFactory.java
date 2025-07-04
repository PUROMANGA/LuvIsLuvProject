package com.example.luvisluvproject.domain.memberInteractionLog.common;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.memberInteractionLog.dto.MemberInteractionLogDto;
import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberInteractionLog;
import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberTagLikeCount;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberInteractionLogDtoFactory {

	public MemberInteractionLogDto memberInteractionLogDtoOf(MemberInteractionLog memberInteractionLog, List<MemberTagLikeCount> memberTagLikeCount) {

		Double matchScore = matchScoreFunction(memberInteractionLog.getMatchReceivedCount(),
			memberInteractionLog.getMatchRequestCount());

		return new MemberInteractionLogDto(memberInteractionLog, matchScore, memberTagLikeCount);
	}

	public Double matchScoreFunction(Double received, Double requested) {
		if (received == 0 || requested == 0) {
			return 0.0;
		}
		return Math.max(received, requested) / Math.min(received, requested);
	}
}
