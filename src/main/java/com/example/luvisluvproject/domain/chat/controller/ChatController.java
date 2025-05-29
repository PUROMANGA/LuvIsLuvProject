package com.example.luvisluvproject.domain.chat.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.domain.chat.dto.ChatRoomRequestDto;
import com.example.luvisluvproject.domain.chat.service.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor

public class ChatController {

	private final ChatService chatService;

	@MessageMapping("/chat/{chatId}/message")
	public void sendMessage(@DestinationVariable(value = "chatRoomId") Long chatRoomId, @Validated ChatRoomRequestDto chatRoomRequestDto) {

	}
}
