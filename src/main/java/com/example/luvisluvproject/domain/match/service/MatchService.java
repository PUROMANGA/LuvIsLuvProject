package com.example.luvisluvproject.domain.match.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.chat.event.ChatCreateEvent;
import com.example.luvisluvproject.domain.match.common.MatchHandler;
import com.example.luvisluvproject.domain.match.dto.AcceptMatchDto;
import com.example.luvisluvproject.domain.match.dto.MatchResponseDto;
import com.example.luvisluvproject.domain.match.dto.ResponseMatchMemberDto;
import com.example.luvisluvproject.domain.match.entity.Match;
import com.example.luvisluvproject.domain.match.entity.MatchStatus;
import com.example.luvisluvproject.domain.match.repository.MatchRepository;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.memberInteractionLog.event.MemberInteractionLogEvent;
import com.example.luvisluvproject.domain.notify.event.NotifyMatchEvent;
import com.example.luvisluvproject.domain.notify.event.NotifyMatchSubEvent;
import com.example.luvisluvproject.domain.recommendedmembers.handler.RecommendedMembersHandler;
import com.example.luvisluvproject.domain.tag.repository.MemberTagRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

@Service

public class MatchService {

	private final MatchRepository matchRepository;
	private final MemberRepository memberRepository;
	private final RedisTemplate<String, Object> matchRedisTemplate;
	private final RedisTemplate<String, String> customStringRedisTemplate;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final MemberTagRepository memberTagRepository;
	private final MatchHandler matchHandler;
	private final RecommendedMembersHandler recommendedMembersHandler;

	public MatchService(MatchRepository matchRepository, MemberRepository memberRepository,
		RedisTemplate<String, Object> matchRedisTemplate, RedisTemplate<String, String> customStringRedisTemplate,
		ApplicationEventPublisher applicationEventPublisher, MemberTagRepository memberTagRepository,
		MatchHandler matchHandler,
		RecommendedMembersHandler recommendedMembersHandler) {
		this.matchRepository = matchRepository;
		this.memberRepository = memberRepository;
		this.matchRedisTemplate = matchRedisTemplate;
		this.customStringRedisTemplate = customStringRedisTemplate;
		this.applicationEventPublisher = applicationEventPublisher;
		this.memberTagRepository = memberTagRepository;
		this.matchHandler = matchHandler;
		this.recommendedMembersHandler = recommendedMembersHandler;
	}

	/**
	 * 매칭 시스템
	 * @param email
	 * @return
	 */
	@Transactional
	public List<ResponseMatchMemberDto> getMatchMemberListService(String email) {
		System.out.println("====================================================");
		System.out.println("📥 [1단계] 사용자 조회 시작");
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		System.out.println("✅ 사용자 조회 완료: " + me.getEmail());
		System.out.println("====================================================");

		System.out.println("📊 [2단계] 1차 매칭 결과 조회");
		List<ResponseMatchMemberDto> matchMemberDtoList = memberTagRepository.findResponseMatchMemberDtoFindByEmail(
			email);
		System.out.println("🔢 1차 매칭 결과 수: " + matchMemberDtoList.size());
		System.out.println("====================================================");

		System.out.println("🤝 [3단계] 2차 추천 매칭 계산 시작");
		List<ResponseMatchMemberDto> recommendMatchMemberDtoList = matchHandler.getMatchMemberDto(email,
			matchMemberDtoList);
		System.out.println("🔢 2차 매칭 결과 수: " + recommendMatchMemberDtoList.size());
		System.out.println("====================================================");

		matchMemberDtoList.addAll(recommendMatchMemberDtoList);

		List<ResponseMatchMemberDto> distinctMatchMemberDtoList = matchMemberDtoList.stream()
			.collect(Collectors.toMap(
				ResponseMatchMemberDto::getId,
				dto -> dto,
				(dto1, dto2) -> dto1
				// 중복 id가 있으면 첫 번째 걸 유지
			))
			.values()
			.stream()
			.toList();

		System.out.println("✅ [4단계] 중복 제거 후 최종 매칭 대상 수: " + distinctMatchMemberDtoList.size());
		System.out.println("====================================================");

		customStringRedisTemplate.opsForZSet().incrementScore(me.getId().toString(), "GetMatchCount", 1);
		System.out.println("📡 [5단계] 레디스에 매칭 횟수 정보 기록 완료");
		System.out.println("====================================================");

		System.out.println("📦 [6단계] 추천 멤버 기록 저장 시작");
		recommendedMembersHandler.createRecommendedMembers(me, distinctMatchMemberDtoList);
		System.out.println("✅ 저장 완료");
		System.out.println("====================================================");

		return distinctMatchMemberDtoList;
	}

	/**
	 * 매칭을 걸면 해당 사람에게 요청이 갑니다.
	 * @param receiverId
	 * @param email
	 * @return
	 */
	@Transactional
	public MatchResponseDto createMatchService(Long receiverId, String email) {

		System.out.println("====================================================");
		System.out.println("📥 [1단계] 사용자 조회");
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		Member receiverMember = memberRepository.findById(receiverId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		System.out.println("✅ 조회된 유저: sender=" + me.getId() + ", receiver=" + receiverMember.getId());
		System.out.println("====================================================");

		System.out.println("🎯 [2단계] 매칭 객체 생성 및 저장");
		receiverMember.plusIsLike();
		Match match = new Match(me.getId(), receiverId);
		// String redisKey = receiverMember.getId().toString();
		//
		// matchRedisTemplate.opsForSet().add(redisKey, match);
		matchRepository.save(match);
		System.out.println("✅ 매칭 객체 저장 완료 (MatchId는 DB에서 자동 생성됨)");
		System.out.println("====================================================");

		System.out.println("📨 [3단계] 이벤트 발행");
		applicationEventPublisher.publishEvent(new NotifyMatchSubEvent(this, me, receiverMember));
		applicationEventPublisher.publishEvent(new MemberInteractionLogEvent(this, me, receiverMember));
		System.out.println("✅ 이벤트 발행 완료");
		System.out.println("====================================================");

		System.out.println(me.getId() + "의 매칭 요청 수를 수집합니다.");
		customStringRedisTemplate.opsForZSet().incrementScore(me.getId().toString(), "MatchRequestCount", 1);

		System.out.println("====================================================");

		System.out.println(receiverMember.getId() + "의 매칭 수신 횟수를 수집합니다.");
		customStringRedisTemplate.opsForZSet()
			.incrementScore(receiverMember.getId().toString(), "MatchReceivedCount", 1);
		System.out.println("====================================================");

		return new MatchResponseDto(me, receiverMember, match);
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
		System.out.println("====================================================");
		System.out.println("🔄 [1단계] 수락/거절 요청 수신 및 사용자 조회");

		Member sender = memberRepository.findById(senderId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		System.out.println("✅ senderId: " + senderId + ", me(email): " + email);
		System.out.println("====================================================");

		System.out.println("🔧 [2단계] 매칭 상태 업데이트");
		Match newMatch = matchRepository.findBySenderIdAndReceiverId(sender.getId(), me.getId());
		newMatch.updateMatchingStatus(acceptMatchDto);
		matchRepository.save(newMatch);
		System.out.println("✅ 상태 업데이트 완료: " + acceptMatchDto.getMatchStatus());
		System.out.println("====================================================");

		if (acceptMatchDto.getMatchStatus().equals(MatchStatus.ACCEPTED)) {
			System.out.println("💬 [3단계] 매칭 수락 → 채팅방 생성 및 이벤트 발행");
			applicationEventPublisher.publishEvent(new ChatCreateEvent(this, sender.getId(), me.getId()));
			applicationEventPublisher.publishEvent(new NotifyMatchEvent(this, me, sender));
			applicationEventPublisher.publishEvent(new MemberInteractionLogEvent(this, me, sender));
			System.out.println("✅ 채팅방 생성 및 알림 이벤트 완료");
			System.out.println("====================================================");
		} else {
			System.out.println("❌ 매칭 거절 처리 완료");
			System.out.println("====================================================");
		}

		return new MatchResponseDto(sender, me, newMatch);
	}

	/**
	 * 내가 받은 match를 전부 SLICE로 가져옵니다.
	 * @param email
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public Slice<MatchResponseDto> getMatchService(String email, Pageable pageable) {

		System.out.println("====================================================");
		System.out.println("📥 [1단계] 사용자 조회");

		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		System.out.println("✅ 사용자 조회 완료: id=" + me.getId());
		System.out.println("====================================================");

		System.out.println("🔍 [2단계] 매칭 요청 목록 조회");
		Slice<Match> matches = matchRepository.findByReceiverId(me.getId(), pageable);
		System.out.println("✅ 조회된 매칭 수: " + matches.getNumberOfElements());
		System.out.println("====================================================");

		System.out.println("🎯 [3단계] Match -> MatchResponseDto 변환 시작");

		Slice<MatchResponseDto> matchDtos = matches.map(match -> {
			Member sender = memberRepository.findById(match.getSenderId())
				.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
			System.out.println("  - 변환 중: MatchId=" + match.getId() +
				", SenderId=" + sender.getId() + ", ReceiverId=" + me.getId());
			return new MatchResponseDto(sender, me, match);
		});

		System.out.println("✅ 모든 매칭 응답 변환 완료");
		System.out.println("====================================================");

		return matchDtos;
	}
}