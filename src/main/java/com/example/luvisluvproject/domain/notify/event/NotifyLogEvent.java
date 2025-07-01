package com.example.luvisluvproject.domain.notify.event;

import org.springframework.context.ApplicationEvent;

import com.example.luvisluvproject.domain.member.entity.Member;

import lombok.Getter;

@Getter
public class NotifyLogEvent extends ApplicationEvent {
	private final Long memberId;

	public NotifyLogEvent(Object source, Long memberId) {
		super(source);
		this.memberId = memberId;
	}
}
