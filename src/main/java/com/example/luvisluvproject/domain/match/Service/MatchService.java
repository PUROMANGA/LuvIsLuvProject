package com.example.luvisluvproject.domain.match.Service;

import org.springframework.stereotype.Service;

import com.example.luvisluvproject.domain.match.dto.MatchRequestDto;
import com.example.luvisluvproject.domain.match.dto.MatchResponseDto;
import com.example.luvisluvproject.domain.match.entity.Match;
import com.example.luvisluvproject.domain.match.repository.MatchRepository;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ErrorCode;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class MatchService {

	private final MatchRepository matchRepository;
	private final MemberRepository memberRepository;

	public MatchResponseDto createMatchService(MatchRequestDto matchRequestDto, String email) {
		boolean isLike = false;
		//senderId(로그인된 유저)
		Member senderMember = memberRepository.findByEmail(email);
		//receiverId(좋아요 받은 유저)
		Member receiverMember = memberRepository.findById(matchRequestDto.getReceiverId()).orElseThrow(() -> new CustomRuntimeException(
			ExceptionCode.USER_CANT_FIND));
		//match(수락 안 된 상태) 객체 생성
		Match match = new Match(senderMember.getId(), receiverMember.getId(), isLike);
		//match를 저장합니다.
		matchRepository.save(match);
		return new MatchResponseDto(match);
	}
}
