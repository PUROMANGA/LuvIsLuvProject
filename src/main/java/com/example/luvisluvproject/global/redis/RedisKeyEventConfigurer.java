package com.example.luvisluvproject.global.redis;

import java.nio.charset.StandardCharsets;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisKeyEventConfigurer {

	private final RedisConnectionFactory redisConnectionFactory;

	@PostConstruct
	public void init() {
		redisConnectionFactory.getConnection()
			.execute("CONFIG", "SET".getBytes(StandardCharsets.UTF_8), "notify-keyspace-events".getBytes(
				StandardCharsets.UTF_8), "Ex".getBytes(StandardCharsets.UTF_8));
	}
}
