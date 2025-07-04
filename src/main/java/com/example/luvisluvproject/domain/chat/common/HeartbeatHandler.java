package com.example.luvisluvproject.domain.chat.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
public class HeartbeatHandler {
	private final RedisTemplate<String, Object> redisTemplate;
	private final RedisTemplate<String, String> stompRedistemplate;
	Map<String, Long> lastHeartbeatMap = new ConcurrentHashMap<>();

	public HeartbeatHandler(RedisTemplate<String, Object> redisTemplate, @Qualifier("customStringRedisTemplate")RedisTemplate<String, String> stompRedistemplate) {
		this.redisTemplate = redisTemplate;
		this.stompRedistemplate = stompRedistemplate;
	}

	public void updateHeartbeat(String email) {
		lastHeartbeatMap.put(email, System.currentTimeMillis());
	}

	@Scheduled(fixedDelay = 15000)
	public void checkHeartbeatTimeout() {
		long now = System.currentTimeMillis();
		lastHeartbeatMap.forEach((email, lastSeen) -> {
			if (now - lastSeen > 30000) {
				String webSocketSessionId = stompRedistemplate.opsForValue().get(email);
				redisTemplate.delete(webSocketSessionId);
			}
		});
	}
}
