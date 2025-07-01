package com.example.luvisluvproject.domain.memberInteractionLog.common;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.memberInteractionLog.dto.MemberInteractionLogDto;
import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberInteractionLog;
import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberTagLikeCount;
import com.example.luvisluvproject.domain.memberInteractionLog.repository.MemberTagLikeCountRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberInteractionLogDtoFactory {

	private final MemberTagLikeCountRepository memberTagLikeCountRepository;

	public MemberInteractionLogDto memberInteractionLogDtoOf(MemberInteractionLog memberInteractionLog) {

		Map<String, Double> tags = memberTagLikeCountRepository.findAllByMemberId(
			memberInteractionLog.getMemberId()).stream().collect(Collectors.toMap(MemberTagLikeCount::getTagName, MemberTagLikeCount::getTagCount));

		Double matchScore = matchScoreFunction(memberInteractionLog.getMatchReceivedCount(),
			memberInteractionLog.getMatchRequestCount());

		return new MemberInteractionLogDto(memberInteractionLog, matchScore, tags);
	}

	public Double matchScoreFunction(Double received, Double requested) {
		if (received == 0 || requested == 0) {
			return 0.0;
		}
		return Math.max(received, requested) / Math.min(received, requested);
	}
}
