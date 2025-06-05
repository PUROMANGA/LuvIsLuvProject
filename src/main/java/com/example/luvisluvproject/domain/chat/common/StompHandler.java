package com.example.luvisluvproject.domain.chat.common;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.chat.entity.ChatRoom;
import com.example.luvisluvproject.domain.chat.repository.ChatRoomRepository;
import com.example.luvisluvproject.global.config.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j

public class StompHandler implements ChannelInterceptor {

	private final ChatRoomRepository chatRoomRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final JwtUtil jwtUtil;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		if (StompCommand.CONNECT == accessor.getCommand()) {
			String authToken = accessor.getFirstNativeHeader("Authorization");
			String token = authToken.substring(7);

			if (authToken == null || !authToken.startsWith("Bearer ")) {
				throw new IllegalArgumentException("Authorization 헤더가 없거나 형식이 잘못되었습니다.");
			}

			if (!jwtUtil.validateToken(token)) {
				throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
			}

			String username = jwtUtil.extractClaims(token).getSubject();
			accessor.setUser(() -> username);

		} else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {

			String destination = accessor.getDestination();

			//세션아이디
			String sessionId = accessor.getSessionId();

			String subscriptionId = accessor.getSubscriptionId();

			//방 아이디
			String roomId = destination.substring(destination.lastIndexOf("/") + 1);
			ChatRoom chatRoom = chatRoomRepository.findById(Long.parseLong(roomId))
				.orElseThrow(() -> new RuntimeException("채팅방이 존재하지 않습니다."));

			//Redis-key
			String key = sessionId + subscriptionId;
			String value = roomId;
			//세션과 방 아이디를 저장
			redisTemplate.opsForSet().add(key, value);

		} else if (StompCommand.UNSUBSCRIBE == accessor.getCommand()) {
			String sessionId = accessor.getSessionId();
			String subscriptionId = accessor.getSubscriptionId();
			redisTemplate.delete(sessionId + subscriptionId);
		}
		return message;
	}
}
