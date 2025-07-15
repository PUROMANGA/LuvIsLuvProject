package com.example.luvisluvproject.domain.tag.event;

import java.util.List;
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
public class MemberTagDeleteEventListener {

	private final MemberTagRepository memberTagRepository;

	@Async
	@EventListener
	@Transactional
	public void handlerMemberTagDelete(MemberTagDeleteEvent memberTagDeleteEvent) {
		Long memberId = memberTagDeleteEvent.getMemberId();

		List<Long> tagIds = memberTagDeleteEvent.getDeleteTags().stream().map(Tag::getId).toList();

		List<MemberTag> deletedMemberTags = memberTagRepository.findByMemberIdAndTagIdIn(memberId, tagIds);
		memberTagRepository.deleteAll(deletedMemberTags);
	}
}
