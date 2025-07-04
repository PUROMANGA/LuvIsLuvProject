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
	private final MemberTagRepository memberTagRepository;

	@Transactional
	public List<ResponseMatchMemberDto> getMatchMemberDto(String email, List<ResponseMatchMemberDto> matchMemberDtoList) {
		System.out.println("====================================================");
		System.out.println("🧮 [getMatchMemberDto] 2차 추천 로직 실행 시작");

		List<ResponseMatchMemberDto> forReturnResponseMatchMemberDto = new ArrayList<>();

		List<Long> ids = matchMemberDtoList.stream().map(ResponseMatchMemberDto::getId).toList();
		List<String> matchMember = memberRepository.findAllById(ids).stream().map(Member::getEmail).toList();

		System.out.println("📨 각 사용자에 대해 2차 추천 수행 중...");
		System.out.println("====================================================");

		for (String matchMemberEmail : matchMember) {
			forReturnResponseMatchMemberDto.addAll(
				memberTagRepository.findCoRecommendedMembersByMeEmailAndEmail(email, matchMemberEmail)
			);
		}

		System.out.println("✅ [getMatchMemberDto] 2차 추천 완료. 총 추천 수: " + forReturnResponseMatchMemberDto.size());
		System.out.println("====================================================");

		return forReturnResponseMatchMemberDto;
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
