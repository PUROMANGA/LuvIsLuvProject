package com.example.luvisluvproject.domain.tag.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.tag.common.TagHandler;
import com.example.luvisluvproject.domain.tag.dto.TagRequestDto;
import com.example.luvisluvproject.domain.tag.entity.MemberTag;
import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.domain.tag.event.MemberTagCreateEvent;
import com.example.luvisluvproject.domain.tag.event.MemberTagDeleteEvent;
import com.example.luvisluvproject.domain.tag.repository.MemberTagRepository;
import com.example.luvisluvproject.global.textCleaner.TextValidator;

@Service
public class TagService {
	private final MemberRepository memberRepository;
	private final MemberTagRepository memberTagRepository;
	private final RedisTemplate<String, Tag> tagRedisTemplate;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final TagHandler tagHandler;
	private final TextValidator textValidator;


	public TagService(MemberRepository memberRepository,
		MemberTagRepository memberTagRepository,
		@Qualifier("tagRedisTemplate") RedisTemplate<String, Tag> tagRedisTemplate,
		ApplicationEventPublisher applicationEventPublisher, TagHandler tagHandler, TextValidator textValidator) {
		this.memberRepository = memberRepository;
		this.memberTagRepository = memberTagRepository;
		this.tagRedisTemplate = tagRedisTemplate;
		this.applicationEventPublisher = applicationEventPublisher;
		this.tagHandler = tagHandler;
		this.textValidator = textValidator;
	}

	/**
	 * 태그 등록, 수정, 삭제
	 */
	@Transactional
	public void syncTags(List<TagRequestDto> requestDtos, String email) {

		//setUp
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

		//멤버가 소지하고 있는 태그 수
		int countingMemberTag = memberRepository.findTagCountById(me.getId());

		//태그로 매핑해준 값
		Set<Tag> requestTags = requestDtos.stream().map(Tag::new).collect(Collectors.toSet());

		//요청으로 들어온 태그 이름들
		Set<String> requestTagNames = tagHandler.mapSetTagName(requestTags);

		//레디스의 태그
		Set<Tag> redisTags = tagRedisTemplate.opsForSet().members("Tag");

		//db의 태그
		Set<MemberTag> dbTags = new HashSet<>(memberTagRepository.findAllByMemberId(me.getId()));

		//레디스, db의 태그로 만든 현재 서비스 내 저장된 모든 태그
		Set<Tag> savedTags = tagHandler.creatTagSet(redisTags, dbTags);
		savedTags.addAll(requestTags);

		//로직
		if (countingMemberTag < 30) {

			Set<String> analyzerTagName = textValidator.analyzerName(requestTagNames);
			if(!textValidator.analyzerText(analyzerTagName)) {
				throw new RuntimeException("금지단어가 포함되어 있습니다.");
			}

			Set<Tag> deleteTags = savedTags.stream()
				.filter(t -> !requestTagNames.contains(t.getName()))
				.collect(Collectors.toSet());

			applicationEventPublisher.publishEvent(new MemberTagDeleteEvent(this, me.getId(), deleteTags));
			applicationEventPublisher.publishEvent(new MemberTagCreateEvent(this, me.getId(), savedTags));

			int countingAfterSavedMemberTag = memberRepository.findTagCountById(me.getId());

			if (countingAfterSavedMemberTag > 30) {
				throw new RuntimeException("태그는 30개 이상 가질 수 없습니다.");
			}

			tagRedisTemplate.opsForSet().add("Tag", savedTags.toArray(new Tag[0]));
		}
	}
}
