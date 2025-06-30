package com.example.luvisluvproject.domain.chat.dto;

import lombok.Getter;

@Getter

public class ResponseChatRoom {
	private final Long chatRoomId;
	private final String memberName;
	private final String messageContent;

	public ResponseChatRoom(Long chatRoomId, String memberName, String messageContent) {
		this.chatRoomId = chatRoomId;
		this.memberName = memberName;
		this.messageContent = messageContent;
	}
}
