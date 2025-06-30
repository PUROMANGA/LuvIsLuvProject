package com.example.luvisluvproject.domain.tag.event;

import java.util.Set;

import org.springframework.context.ApplicationEvent;

import com.example.luvisluvproject.domain.tag.entity.Tag;

import lombok.Getter;

@Getter
public class MemberTagCreateEvent extends ApplicationEvent {
	private final Long memberId;
	private final Set<Tag> savedTags;

	public MemberTagCreateEvent(Object source, Long memberId, Set<Tag> savedTags) {
		super(source);
		this.memberId = memberId;
		this.savedTags = savedTags;
	}
}
