package com.example.luvisluvproject.domain.memberInteractionLog.event;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.memberInteractionLog.repository.MemberInteractionLogRepository;
import com.example.luvisluvproject.domain.tag.entity.MemberTag;
import com.example.luvisluvproject.domain.tag.enums.TagCategory;
import com.example.luvisluvproject.domain.tag.repository.MemberTagRepository;

@Component

public class MemberInteractionLogEventListener {

	private final MemberRepository memberRepository;
	private final MemberTagRepository memberTagRepository;
	private final RedisTemplate<String, String> stringRedisTemplate;
	private final MemberInteractionLogRepository memberInteractionLogRepository;

	public MemberInteractionLogEventListener(MemberRepository memberRepository, MemberTagRepository memberTagRepository,
		@Qualifier("stringRedisTemplate") RedisTemplate<String, String> stringRedisTemplate,
		MemberInteractionLogRepository memberInteractionLogRepository) {
		this.memberRepository = memberRepository;
		this.memberTagRepository = memberTagRepository;
		this.stringRedisTemplate = stringRedisTemplate;
		this.memberInteractionLogRepository = memberInteractionLogRepository;
	}

	@Async
	@EventListener
	@Transactional
	public void handlerMemberInteractionLogEvent(MemberInteractionLogEvent memberInteractionLogEvent) {

		Member me = memberInteractionLogEvent.getMe();
		Member opponent = memberInteractionLogEvent.getOpponent();

		List<MemberTag> memberTags = memberTagRepository.findAllByMemberId(opponent.getId());

		List<MemberTag> filterTags = memberTags.stream()
			.filter(t -> t.getCategory() == TagCategory.SEXUAL_ORIENTATION )
			.toList();

		for (MemberTag memberTag : filterTags) {
			stringRedisTemplate.opsForZSet().incrementScore(me.getId() + "forTag", memberTag.getTagName(), 1);
		}
	}
}
