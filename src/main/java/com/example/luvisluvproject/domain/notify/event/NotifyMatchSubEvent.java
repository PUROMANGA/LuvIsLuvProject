package com.example.luvisluvproject.domain.notify.event;

import org.springframework.context.ApplicationEvent;

import com.example.luvisluvproject.domain.member.entity.Member;

import lombok.Getter;

@Getter
public class NotifyMatchSubEvent extends ApplicationEvent {
	private final Member member;
	private final Member opponent;

	public NotifyMatchSubEvent(Object source, Member member, Member opponent) {
		super(source);
		this.member = member;
		this.opponent = opponent;
	}
}
