package com.example.luvisluvproject.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.luvisluvproject.domain.match.entity.Match;
import com.example.luvisluvproject.domain.tag.dto.CachedTagDto;

@Configuration
public class RedisConfig {

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		return template;
	}

	@Bean
	public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer() {
		return new GenericJackson2JsonRedisSerializer();
	}

	/**
	 * CachedTagDto 전용 RedisTemplate (정적 타입 보장)
	 */
	@Bean
	public RedisTemplate<String, CachedTagDto> cachedTagRedisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, CachedTagDto> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		return template;
	}
}

