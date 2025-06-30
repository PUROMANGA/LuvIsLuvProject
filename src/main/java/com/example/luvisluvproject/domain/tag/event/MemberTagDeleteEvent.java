package com.example.luvisluvproject.domain.tag.event;

import java.util.Set;

import org.springframework.context.ApplicationEvent;

import com.example.luvisluvproject.domain.tag.entity.Tag;

import lombok.Getter;

@Getter
public class MemberTagDeleteEvent extends ApplicationEvent {
	private final Long memberId;
	private final Set<Tag> deleteTags;

	public MemberTagDeleteEvent(Object source, Long memberId, Set<Tag> deleteTags) {
		super(source);
		this.memberId = memberId;
		this.deleteTags = deleteTags;
	}
}
