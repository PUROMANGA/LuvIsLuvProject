package com.example.luvisluvproject.domain.chat.common;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
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

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j

public class StompHandler implements ChannelInterceptor {

	private final RedisTemplate<String, Object> redisTemplate;
	private final RedisTemplate<String, String> stompRedistemplate;
	private final JwtUtil jwtUtil;

	public StompHandler(RedisTemplate<String, Object> redisTemplate,
		@Qualifier("stringRedisTemplate")RedisTemplate<String, String> stompRedistemplate,
		JwtUtil jwtUtil) {
		this.redisTemplate = redisTemplate;
		this.stompRedistemplate = stompRedistemplate;
		this.jwtUtil = jwtUtil;
	}

	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor
			.getAccessor(message, StompHeaderAccessor.class);

		// StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		StompCommand command = accessor.getCommand();

		if (StompCommand.CONNECT == command) {

			String authHeader = accessor.getFirstNativeHeader("Authorization");

			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				try {
					String token = authHeader.substring(7);
					String email = jwtUtil.extractClaims(token).getSubject();

					Authentication authentication =
						new UsernamePasswordAuthenticationToken(email, null, List.of());

					// Spring Security Context에 등록
					SecurityContextHolder.getContext().setAuthentication(authentication);

					// STOMP 세션에 Principal 설정
					accessor.setUser(authentication);

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
				stompRedistemplate.opsForValue().set(email, webSocketSessionId);
				// stompToWebSocketMap.put(email, webSocketSessionId);

				String channelId = destination.substring(destination.lastIndexOf("/") + 1);
				Long roomId = Long.parseLong(channelId);

				String key = webSocketSessionId;
				String value = "유저 : " + email + " : " + "방 아이디 : " + roomId;

				redisTemplate.opsForSet().add(key, value);
			} else {
				System.out.println("❗ principal 정보 없음! 구독 실패 가능성 있음.");
			}

		} else if (StompCommand.UNSUBSCRIBE == command) {
			String webSocketSessionId = (String)accessor.getSessionAttributes().get("webSocketSessionId");
			// String sessionId = accessor.getSessionId();
			String key = webSocketSessionId;

			redisTemplate.delete(key);

		} else if (StompCommand.DISCONNECT == command) {
			String webSocketSessionId = (String)accessor.getSessionAttributes().get("webSocketSessionId");
			// String sessionId = accessor.getSessionId();
			Set<String> keys = redisTemplate.keys(webSocketSessionId);

			redisTemplate.delete(keys);
		}

		System.out.println("=======================\n");
		return message;
	}
}
