package com.example.luvisluvproject.domain.match.common;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.match.dto.AcceptMatchDto;
import com.example.luvisluvproject.domain.match.dto.MatchResponseDto;
import com.example.luvisluvproject.domain.match.entity.Match;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@Getter
@RequiredArgsConstructor
public class MatchHandler {

	private final MemberRepository memberRepository;

	public Match mapMatchForRedis(Set<Object> matchCache, AcceptMatchDto acceptMatchDto, Long senderId) {
		return matchCache.stream()
			.map(obj -> (Match)obj)
			.peek(match -> match.updateMatchingStatus(acceptMatchDto))
			.filter(match -> match.getSenderId().equals(senderId))
			.findFirst()
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MATCH_NOT_FOUND));
	}

	public List<MatchResponseDto> matchResponseDtoList(Set<Object> matchCache) {
		return matchCache.stream()
			.map(obj -> (Match)obj)
			.map(match -> {
				Member sender = memberRepository.findById(match.getSenderId())
					.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
				Member receiver = memberRepository.findById(match.getReceiverId())
					.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
				return new MatchResponseDto(sender, receiver, match);
			})
			.collect(Collectors.toList());
	}


}
