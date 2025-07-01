package com.example.luvisluvproject.domain.memberInteractionLog.event;

import org.springframework.context.ApplicationEvent;

import com.example.luvisluvproject.domain.member.entity.Member;

import lombok.Getter;

@Getter
public class MemberInteractionLogEvent extends ApplicationEvent {
	private final Member me;
	private final Member opponent;

	public MemberInteractionLogEvent(Object source, Member me, Member opponent) {
		super(source);
		this.me = me;
		this.opponent = opponent;
	}
}
