package com.example.luvisluvproject.domain.match.service;

import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.chat.event.ChatCreateEvent;
import com.example.luvisluvproject.domain.match.dto.AcceptMatchDto;
import com.example.luvisluvproject.domain.match.dto.MatchResponseDto;
import com.example.luvisluvproject.domain.match.entity.Match;
import com.example.luvisluvproject.domain.match.entity.MatchStatus;
import com.example.luvisluvproject.domain.match.repository.MatchRepository;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class MatchService {

	private final MatchRepository matchRepository;
	private final MemberRepository memberRepository;
	private final RedisTemplate<String, Object> matchRedisTemplate;
	private final ApplicationEventPublisher applicationEventPublisher;

	/**
	 * 매칭을 걸면 해당 사람에게 요청이 갑니다.
	 * @param receiverId
	 * @param email
	 * @return
	 */
	@Transactional
	public MatchResponseDto createMatchService(Long receiverId, String email) {
		//senderId(로그인된 유저)
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		//receiverId(좋아요 받은 유저)
		Member receiverMember = memberRepository.findById(receiverId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		//매칭 요청 받은 사람 인기수치 올림
		receiverMember.plusIsLike();
		//match(수락 안 된 상태) 객체 생성
		Match match = new Match(me.getId(), receiverId);
		//match를 저장합니다.
		String redisKey = me.getId() + ":" + receiverId;
		matchRedisTemplate.opsForSet().add(redisKey, match);
		return new MatchResponseDto(match);
	}

	/**
	 * 해당 매칭이 오고, 매칭의 상태를 '수락' 혹은 '거절'로 변경합니다. 이때 senderId는 현재 로그인된 멤버 자기 자신의 ID입니다.
	 * @param senderId
	 * @param acceptMatchDto
	 * @param email
	 * @return
	 */
	@Transactional
	public MatchResponseDto patchMatchService(Long senderId, AcceptMatchDto acceptMatchDto, String email) {
		//매치에서 내가 아닌 보낸사람 들고오기
		Member sender = memberRepository.findById(senderId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		//나 들고오기
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		//
		Set<Object> matchCache = matchRedisTemplate.opsForSet().members(sender.getId() + ":" + me.getId());
		Match newMatch = matchCache.stream().map(obj -> {
				Match m = (Match)obj;
				m.updateMatchingStatus(acceptMatchDto);
				return m;
			})
			.findFirst()
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MATCH_NOT_FOUND));

		matchRedisTemplate.delete(sender.getId() + ":" + me.getId());

		if (acceptMatchDto.getMatchStatus().equals(MatchStatus.ACCEPTED)) {
			applicationEventPublisher.publishEvent(new ChatCreateEvent(this, sender.getId(), me.getId()));
		}

		return new MatchResponseDto(matchRepository.save(newMatch));
	}

	/**
	 * 내가 받은 match를 전부 SLICE로 가져옵니다.
	 * @param email
	 * @param pageable
	 * @return
	 */

	@Transactional(readOnly = true)
	public Slice<MatchResponseDto> getMatchService(String email, Pageable pageable) {
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		Slice<Match> matches = matchRepository.findMatchByReceiverId(me.getId(), pageable);

		return matches.map(MatchResponseDto::new);
	}
}