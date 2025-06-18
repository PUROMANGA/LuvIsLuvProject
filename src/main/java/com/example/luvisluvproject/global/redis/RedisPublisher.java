package com.example.luvisluvproject.global.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.example.luvisluvproject.domain.chat.dto.MessageDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service

public class RedisPublisher {

	private final ChannelTopic channelTopic;
	private final RedisTemplate<String, Object> redisTemplate;

	public void publish(MessageDto messageDto) {
		redisTemplate.convertAndSend(channelTopic.getTopic(), messageDto);
	}
}
