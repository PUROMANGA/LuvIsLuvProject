package com.example.luvisluvproject.domain.chat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.example.luvisluvproject.domain.chat.common.HeartbeatHandler;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor

public class HeartbeatController {
	private final HeartbeatHandler heartbeatHandler;

	@MessageMapping("/heartbeats")
	public void heartbeat(StompHeaderAccessor accessor) {
		String email = accessor.getUser().getName();
		heartbeatHandler.updateHeartbeat(email);
	}
}
