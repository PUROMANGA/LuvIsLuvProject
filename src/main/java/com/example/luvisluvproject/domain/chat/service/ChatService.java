package com.example.luvisluvproject.domain.chat.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.luvisluvproject.domain.chat.entity.Message;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class ChatService {

	private final SimpMessagingTemplate simpMessagingTemplate;

	public Long submitRoomId(String destination) {
		if (destination == null) {
			throw new RuntimeException("잘못된 방 주소 입니다");
		}
		String roomId = destination.substring(destination.lastIndexOf("/") + 1);
		return Long.parseLong(roomId);
	}

	public void sendChatEnterMessage(Message message) {
		String destination = "/chats/rooms/" + message.getChatRoom().getId();
		simpMessagingTemplate.convertAndSend(destination, message);
	}
}
