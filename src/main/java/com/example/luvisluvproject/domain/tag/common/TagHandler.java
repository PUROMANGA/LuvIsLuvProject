package com.example.luvisluvproject.domain.tag.common;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.tag.entity.MemberTag;
import com.example.luvisluvproject.domain.tag.entity.Tag;

import lombok.Getter;

@Component
@Getter
public class TagHandler {

	String name;

	public Set<String> mapSetTagName(Set<Tag> requestTags) {
		return requestTags.stream().map(Tag::getName).collect(Collectors.toSet());
	}

	public Set<Tag> creatTagSet(Set<Tag> redisTags, Set<MemberTag> dbTags) {
		Set<Tag> tags = new HashSet<>();
		tags.addAll(redisTags);
		tags.addAll(dbTags.stream().map(Tag::new).collect(Collectors.toSet()));
		return tags;
	}
}
