package com.example.luvisluvproject.domain.match.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.chat.event.ChatCreateEvent;
import com.example.luvisluvproject.domain.match.dto.AcceptMatchDto;
import com.example.luvisluvproject.domain.match.dto.MatchMemberDto;
import com.example.luvisluvproject.domain.match.dto.MatchResponseDto;
import com.example.luvisluvproject.domain.match.dto.ResponseMatchMemberDto;
import com.example.luvisluvproject.domain.match.entity.Match;
import com.example.luvisluvproject.domain.match.entity.MatchStatus;
import com.example.luvisluvproject.domain.match.repository.MatchRepository;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.tag.repository.MemberTagRepository;
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
	private final MemberTagRepository memberTagRepository;

	/**
	 * 매칭 시스템
	 * @param email
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ResponseMatchMemberDto> getMatchMemberListService(String email) {
		List<ResponseMatchMemberDto> matchMemberDtoList = memberTagRepository.findResponseMatchMemberDtoFindByEmail(
			email);
		return matchMemberDtoList;
	}

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
		String redisKey = receiverMember.getId().toString();
		matchRedisTemplate.opsForSet().add(redisKey, match);
		return new MatchResponseDto(match);
	}

	/**
	 * 해당 매칭이 오고, 매칭의 상태를 '수락' 혹은 '거절'로 변경합니다.
	 * @param senderId
	 * @param acceptMatchDto
	 * @param email
	 * @return
	 */
	@Transactional
	public MatchResponseDto patchMatchService(Long senderId, AcceptMatchDto acceptMatchDto, String email) {
		//AcceptMatchDto를 분석해서 아닌 로직은 딱히 실행하지 않아도 되니 성능을 위해서 두 가지로 분류해서 처리
		//먼저 나와 상대를 찾아주고
		Member sender = memberRepository.findById(senderId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		//매칭을 받을시 로직
		if (acceptMatchDto.getMatchStatus().equals(MatchStatus.ACCEPTED)) {
			Set<Object> matchCache = matchRedisTemplate.opsForSet().members(me.getId().toString());
			Match newMatch = matchCache.stream()
				.map(obj -> (Match)obj)
				.peek(match -> match.updateMatchingStatus(acceptMatchDto))
				.filter(match -> match.getSenderId().equals(senderId))
				.findFirst()
				.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MATCH_NOT_FOUND));
			matchRedisTemplate.delete(me.getId().toString());
			applicationEventPublisher.publishEvent(new ChatCreateEvent(this, sender.getId(), me.getId()));
			return new MatchResponseDto(matchRepository.save(newMatch));
		} else {
			//나 들고오기
			matchRedisTemplate.delete(me.getId().toString());
			return new MatchResponseDto(senderId, sender.getName(), me.getId(), me.getName(), MatchStatus.REJECTED);
		}
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

		Set<Object> matches = matchRedisTemplate.opsForSet().members(me.getId().toString());

		List<MatchResponseDto> matchCache = matches.stream()
			.map(obj -> (Match)obj)
			.map(match -> {
				String senderName = memberRepository.findById(match.getSenderId())
					.map(Member::getName)
					.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
				String receiverName = memberRepository.findById(match.getReceiverId())
					.map(Member::getName)
					.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
				return new MatchResponseDto(match, senderName, receiverName);
			})
			.collect(Collectors.toList());

		int start = (int)pageable.getOffset(); // = page * size
		int end = Math.min(start + pageable.getPageSize(), matchCache.size());

		List<MatchResponseDto> pageContent = matchCache.subList(start, end);
		boolean hasNext = end < matchCache.size(); // 다음 페이지 존재 여부

		return new SliceImpl<>(pageContent, pageable, hasNext);
	}
}