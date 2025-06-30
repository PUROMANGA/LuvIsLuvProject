package com.example.luvisluvproject.domain.chat.common;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.luvisluvproject.global.config.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j

public class StompHandler implements ChannelInterceptor {

	private final RedisTemplate<String, Object> redisTemplate;
	private final Map<String, String> stompToWebSocketMap;
	private final JwtUtil jwtUtil;

	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor
			.getAccessor(message, StompHeaderAccessor.class);

		// StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		StompCommand command = accessor.getCommand();

		System.out.println("\n=======================");
		System.out.println("💬 STOMP Command: " + command);
		System.out.println("📎 Session ID: " + accessor.getSessionId());

		if (StompCommand.CONNECT == command) {
			System.out.println("🟢 CONNECT 요청 시작");

			String authHeader = accessor.getFirstNativeHeader("Authorization");
			System.out.println("🔐 Authorization 헤더: " + authHeader);

			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				try {
					String token = authHeader.substring(7);
					String email = jwtUtil.extractClaims(token).getSubject();

					System.out.println("📧 추출된 이메일: " + email);

					Authentication authentication =
						new UsernamePasswordAuthenticationToken(email, null, List.of());

					System.out.println("🔒 Authentication 객체 생성됨: " + authentication);
					System.out.println("🔒 getName(): " + authentication.getName());
					System.out.println("🔒 isAuthenticated(): " + authentication.isAuthenticated());

					// Spring Security Context에 등록
					SecurityContextHolder.getContext().setAuthentication(authentication);
					System.out.println("✅ SecurityContextHolder에 등록 완료");

					// STOMP 세션에 Principal 설정
					accessor.setUser(authentication);
					System.out.println("✅ accessor.setUser() 완료");

				} catch (Exception e) {
					System.out.println("❌ 토큰 파싱 또는 인증 처리 실패: " + e.getMessage());
					throw e;
				}
			} else {
				System.out.println("❗ Authorization 헤더 없음 또는 형식 불일치");
			}

		} else if (StompCommand.SUBSCRIBE == command) {
			System.out.println("📡 SUBSCRIBE 요청 수신");

			Principal principal = accessor.getUser();

			if (principal != null) {
				String email = principal.getName();
				String destination = accessor.getDestination();
				String webSocketSessionId = (String)accessor.getSessionAttributes().get("webSocketSessionId");
				System.out.println("webSocketSessionId = " + webSocketSessionId);
				stompToWebSocketMap.put(email, webSocketSessionId);

				System.out.println("➡️ 구독 Destination: " + destination);

				String channelId = destination.substring(destination.lastIndexOf("/") + 1);
				Long roomId = Long.parseLong(channelId);

				System.out.println("👤 구독 요청한 유저: " + email);

				String key = webSocketSessionId;
				String value = "유저 : " + email + " : " + "방 아이디 : " + roomId;

				redisTemplate.opsForSet().add(key, value);
				System.out.println("✅ Redis 저장 완료");

			} else {
				System.out.println("❗ principal 정보 없음! 구독 실패 가능성 있음.");
			}

		} else if (StompCommand.UNSUBSCRIBE == command) {
			String webSocketSessionId = (String)accessor.getSessionAttributes().get("webSocketSessionId");
			// String sessionId = accessor.getSessionId();
			String key = webSocketSessionId;

			redisTemplate.delete(key);
			System.out.println("🗑️ UNSUBSCRIBE 요청 처리 → Redis 삭제 key: " + key);

		} else if (StompCommand.DISCONNECT == command) {
			String webSocketSessionId = (String)accessor.getSessionAttributes().get("webSocketSessionId");
			// String sessionId = accessor.getSessionId();
			Set<String> keys = redisTemplate.keys(webSocketSessionId);

			redisTemplate.delete(keys);
			System.out.println("📴 DISCONNECT 처리 → 삭제된 Redis 키 목록: " + keys);
		}

		System.out.println("=======================\n");
		return message;
	}
}
