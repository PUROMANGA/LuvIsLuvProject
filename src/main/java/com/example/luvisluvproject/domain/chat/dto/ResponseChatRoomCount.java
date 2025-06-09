package com.example.luvisluvproject.domain.chat.dto;

import lombok.Getter;

@Getter

public class ResponseChatRoomCount {
	private int chatCount;

	public ResponseChatRoomCount(int chatCount) {
		this.chatCount = chatCount;
	}
}
