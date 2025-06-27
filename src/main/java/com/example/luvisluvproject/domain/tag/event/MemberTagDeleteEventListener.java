package com.example.luvisluvproject.domain.tag.event;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.tag.entity.MemberTag;
import com.example.luvisluvproject.domain.tag.repository.MemberTagRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberTagDeleteEventListener {

	private final MemberTagRepository memberTagRepository;
	private final MemberRepository memberRepository;

	@EventListener
	public void handlerMemberTagDelete(MemberTagDeleteEvent memberTagDeleteEvent) {
		Long memberId = memberTagDeleteEvent.getMemberId();

		Set<MemberTag> tagSet = memberTagDeleteEvent.getDeleteTags().stream().map(t -> new MemberTag(memberId, t.getName(), t.getCategory())).collect(
			Collectors.toSet());

		Set<String> tagNames = tagSet.stream().map(MemberTag::getTagName).collect(Collectors.toSet());

		if(memberTagRepository.existsByMemberIdAndTagNameIn(memberId, tagNames)) {
			throw new CustomRuntimeException(ExceptionCode.MEMBER_TAG_ALREADY_EXISTS);
		}

		memberTagRepository.deleteAll(tagSet);

		Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		member.subTagCount(tagSet.size());
		memberRepository.save(member);
	}
}
