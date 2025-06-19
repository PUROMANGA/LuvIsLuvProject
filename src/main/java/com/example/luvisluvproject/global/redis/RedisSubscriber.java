package com.example.luvisluvproject.global.redis;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.example.luvisluvproject.domain.chat.dto.MessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
public class RedisSubscriber {
	private final ObjectMapper objectMapper;
	private final SimpMessageSendingOperations messagingTemplate;

	public void sendMessage(String publishMessage) {
		try {
			MessageDto messageDto = objectMapper.readValue(publishMessage, MessageDto.class);
			messagingTemplate.convertAndSend("/sub/chats/" + messageDto.getRoomId(), messageDto);
			System.out.println("Redis 받은 메시지: " + messageDto.getContent());
		} catch (Exception e) {
			System.out.println("RedisSubscriber 메시지 처리 실패: " + e.getMessage());
		}
	}
}

