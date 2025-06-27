package com.example.luvisluvproject.global.sse.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.luvisluvproject.global.common.AuthUser;
import com.example.luvisluvproject.global.sse.service.SseEmitterService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SSEController {
	private final SseEmitterService sseEmitterService;

	@GetMapping(value = "/sse/sub", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<SseEmitter> subscribe(
		@AuthenticationPrincipal AuthUser user) {
		SseEmitter emitter = sseEmitterService.subscribe(user.getUsername());
		return ResponseEntity.ok(emitter);
	}
}
