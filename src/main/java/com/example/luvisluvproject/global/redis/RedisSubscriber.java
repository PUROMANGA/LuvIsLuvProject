package com.example.luvisluvproject.global.redis;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.example.luvisluvproject.domain.chat.dto.MessageDto;
import com.example.luvisluvproject.domain.notify.dto.NotifyDto;
import com.example.luvisluvproject.global.sse.service.SseEmitterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
public class RedisSubscriber {
	private final ObjectMapper objectMapper;
	private final SimpMessageSendingOperations messagingTemplate;
	private final SseEmitterService sseEmitterService;

	public void sendMessage(String publishMessage) {
		try {
			MessageDto messageDto = objectMapper.readValue(publishMessage, MessageDto.class);
			messagingTemplate.convertAndSend("/sub/chats/" + messageDto.getRoomId(), messageDto);
		} catch (Exception e) {
			System.out.println("RedisSubscriber 메시지 처리 실패: " + e.getMessage());
		}
	}

	public void sendNotify(String publishNotify) {
		try {
			NotifyDto notifyDto = objectMapper.readValue(publishNotify, NotifyDto.class);
			sseEmitterService.sendToClient(notifyDto.getReceivedId(), notifyDto);
		} catch (Exception e) {
			System.out.println("RedisSubscriber 알림: " + e.getMessage());
		}
	}
}

