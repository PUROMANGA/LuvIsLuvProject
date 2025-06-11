package com.example.luvisluvproject.domain.chat.dto;

import lombok.Getter;

@Getter

public class ResponseChatRoom {
	private Long chatRoomId;
	private String memberName;
	private String messageContent;

	public ResponseChatRoom(Long chatRoomId, String memberName, String messageContent) {
		this.chatRoomId = chatRoomId;
		this.memberName = memberName;
		this.messageContent = messageContent;
	}
}
