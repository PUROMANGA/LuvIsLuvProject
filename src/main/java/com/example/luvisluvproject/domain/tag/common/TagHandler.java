package com.example.luvisluvproject.domain.tag.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.tag.entity.MemberTag;
import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.domain.tag.repository.MemberTagRepository;
import com.example.luvisluvproject.domain.tag.repository.TagJpaRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@Getter
@RequiredArgsConstructor
public class TagHandler {

	private final TagJpaRepository tagJpaRepository;
	private final MemberTagRepository memberTagRepository;

	public Set<String> mapSetTagName(Set<Tag> requestTags) {
		return requestTags.stream().map(Tag::getName).collect(Collectors.toSet());
	}

	public void savedTag(Set<Tag> requestTags) {

		List<Tag> savedTagList = new ArrayList<>();

		for (Tag tag : requestTags) {
			if (!tagJpaRepository.existsByName(tag.getName())) {
				savedTagList.add(tag);
			}
		}

		tagJpaRepository.saveAll(savedTagList);
	}

	public Set<Tag> createDeletedTagSet(Set<Tag> savedTags, Set<String> requestTagNames) {
		return savedTags.stream()
			.filter(t -> !requestTagNames.contains(t.getName()))
			.collect(Collectors.toSet());
	}

	public Set<Tag> createSavedTagSet(Set<Tag> savedTags, Set<Tag> requestTags) {
		return requestTags.stream().filter(t -> !savedTags.contains(t)).collect(Collectors.toSet());
	}

	//DB에서 멤버 태그 추출해준 다음 tag로 바꿔주는 로직
	public Set<Tag> setTag(Member me) {
		Set<MemberTag> dbTags = new HashSet<>(memberTagRepository.findAllByMemberId(me.getId()));
		return dbTags.stream().map(Tag::new).collect(Collectors.toSet());
	}
}
