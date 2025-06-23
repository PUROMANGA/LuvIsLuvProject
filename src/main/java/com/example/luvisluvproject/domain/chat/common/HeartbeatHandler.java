package com.example.luvisluvproject.domain.chat.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HeartbeatHandler {
	Map<String, Long> lastHeartbeatMap = new ConcurrentHashMap<>();
	private final RedisTemplate<String, Object> redisTemplate;
	private final Map<String, String> stompToWebSocketMap;

	public void updateHeartbeat(String email) {
		lastHeartbeatMap.put(email, System.currentTimeMillis());
	}

	@Scheduled(fixedDelay = 15000)
	public void checkHeartbeatTimeout() {
		long now = System.currentTimeMillis();
		lastHeartbeatMap.forEach((email, lastSeen) -> {
			if(now - lastSeen > 30000) {
				String webSocketSessionId = stompToWebSocketMap.get(email);
				redisTemplate.delete(webSocketSessionId);
			}
		});
	}
}
