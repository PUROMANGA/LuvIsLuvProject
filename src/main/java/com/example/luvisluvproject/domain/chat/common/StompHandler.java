package com.example.luvisluvproject.domain.chat.common;

import java.security.Principal;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.chat.repository.ChatRoomRepository;
import com.example.luvisluvproject.global.common.AuthUser;
import com.example.luvisluvproject.global.common.UserDetailsServiceImpl;
import com.example.luvisluvproject.global.config.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j

public class StompHandler implements ChannelInterceptor {

	private final RedisTemplate<String, Object> redisTemplate;
	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		System.out.println("💬 STOMP command: " + accessor.getCommand());

		if (StompCommand.CONNECT == accessor.getCommand()) {
			System.out.println("연결시작");
			String authToken = accessor.getFirstNativeHeader("Authorization");
			String token = authToken.substring(7);
			System.out.println("token = " + token);

			if (!authToken.startsWith("Bearer ")) {
				throw new IllegalArgumentException("Authorization 헤더가 없거나 형식이 잘못되었습니다.");
			}

			if (!jwtUtil.validateToken(token)) {
				throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
			}
		} else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {

			String destination = accessor.getDestination();

			//세션아이디
			String sessionId = accessor.getSessionId();
			//구독 아이디
			String subscriptionId = accessor.getSubscriptionId();
			//방 아이디
			String channelId = destination.substring(destination.lastIndexOf("/") + 1);
			Long roomId = Long.parseLong(channelId);

			Principal principal = accessor.getUser();
			String email = principal.getName();

			//redis-key
			String key = sessionId + subscriptionId;
			String value = "유저 : " + email + "방 아이디 : " + roomId;
			//세션과 방 아이디를 저장
			redisTemplate.opsForSet().add(key, value);

		} else if (StompCommand.UNSUBSCRIBE == accessor.getCommand()) {
			String sessionId = accessor.getSessionId();
			String subscriptionId = accessor.getSubscriptionId();
			redisTemplate.delete(sessionId + subscriptionId);
		} else if (StompCommand.DISCONNECT == accessor.getCommand()) {
			String sessionId = accessor.getSessionId();
			Set<String> keys = redisTemplate.keys(sessionId + "*");
			redisTemplate.delete(keys);
		}
		return message;
	}
}
