package com.example.luvisluvproject.domain.tag.event;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.tag.entity.MemberTag;
import com.example.luvisluvproject.domain.tag.repository.MemberTagRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberTagCreateEventListener {

	private final MemberTagRepository memberTagRepository;
	private final MemberRepository memberRepository;

	@EventListener
	@Transactional
	public void handlerMemberTagCreate(MemberTagCreateEvent memberTagCreateEvent) {
		Long memberId = memberTagCreateEvent.getMemberId();

		Set<MemberTag> tagSet = memberTagCreateEvent.getSavedTags()
			.stream()
			.map(t -> new MemberTag(memberId, t.getName(), t.getCategory()))
			.collect(
				Collectors.toSet());

		Iterator<MemberTag> iterator = tagSet.iterator();

		while (iterator.hasNext()) {
			MemberTag memberTag = iterator.next();
			Optional<MemberTag> foundMemberTag = memberTagRepository.findByMemberIdAndTagName(memberId, memberTag.getTagName());

			if (foundMemberTag.isPresent()) {
				iterator.remove(); // 안전하게 삭제
			}
		}

		memberTagRepository.saveAll(tagSet);

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		member.plusTagCount(tagSet.size());
		memberRepository.save(member);
	}
}

