package com.example.luvisluvproject.domain.chat.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class ChatCreateEvent extends ApplicationEvent {
	private final Long senderId;
	private final Long myId;

	public ChatCreateEvent(Object source, Long senderId, Long myId) {
		super(source);
		this.senderId = senderId;
		this.myId = myId;
	}
}
