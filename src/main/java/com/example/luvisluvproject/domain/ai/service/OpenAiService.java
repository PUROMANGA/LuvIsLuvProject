package com.example.luvisluvproject.domain.ai.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.memberInteractionLog.common.MemberInteractionLogDtoFactory;
import com.example.luvisluvproject.domain.memberInteractionLog.dto.MemberInteractionLogDto;
import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberInteractionLog;
import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberTagLikeCount;
import com.example.luvisluvproject.domain.memberInteractionLog.repository.MemberInteractionLogRepository;
import com.example.luvisluvproject.domain.memberInteractionLog.repository.MemberTagLikeCountRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenAiService {

	private final MemberRepository memberRepository;
	private final MemberInteractionLogRepository memberInteractionLogRepository;
	private final MemberTagLikeCountRepository memberTagLikeCountRepository;
	private final MemberInteractionLogDtoFactory memberInteractionLogDtoFactory;

	@Transactional
	public MemberInteractionLogDto getMemberInteractionLogDto(String email, LocalDate date) {

		//나 찾고
		Member me = memberRepository.findByEmail(email).orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		//이제 여기서 내 이름을 이용해서 actionlog랑 tagsLike찾아줄 거임
		MemberInteractionLog memberInteractionLog = memberInteractionLogRepository.findByMemberIdAndCreatTime(me.getId(), date);
		List<MemberTagLikeCount> memberTagLikeCount = memberTagLikeCountRepository.findAllByMemberIdAndCreatTime(me.getId(), date);

		return memberInteractionLogDtoFactory.memberInteractionLogDtoOf(memberInteractionLog, memberTagLikeCount);
	}
}
