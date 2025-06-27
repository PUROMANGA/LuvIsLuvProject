package com.example.luvisluvproject.global.sse.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Configuration
public class EmitterMapConfig {
	@Bean
	public Map<Long, SseEmitter> emitterMap() {
		return new ConcurrentHashMap<>();
	}
}
