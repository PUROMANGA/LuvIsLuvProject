package com.example.luvisluvproject.domain.tag.event;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.tag.entity.MemberTag;
import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.domain.tag.repository.MemberTagRepository;
import com.example.luvisluvproject.domain.tag.repository.TagJpaRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberTagCreateEventListener {

	private final MemberTagRepository memberTagRepository;
	private final MemberRepository memberRepository;
	private final TagJpaRepository tagJpaRepository;

	@Async
	@EventListener
	@Transactional
	public void handlerMemberTagCreate(MemberTagCreateEvent memberTagCreateEvent) {

		Member member = memberRepository.findById(memberTagCreateEvent.getMemberId())
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		Set<String> tagNameSet = memberTagCreateEvent.getSavedTags()
			.stream()
			.map(Tag::getName)
			.collect(
				Collectors.toSet());

		Set<Tag> tags = tagJpaRepository.findByNameIn(tagNameSet);

		Set<MemberTag> savedMemberTag = new HashSet<>();

		for (Tag tag : tags) {
			MemberTag memberTag = new MemberTag(member, tag);
			savedMemberTag.add(memberTag);
		}

		memberTagRepository.saveAll(savedMemberTag);
	}
}

