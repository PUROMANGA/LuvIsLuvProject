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
		System.out.println("발행합니데이~~~");
		redisTemplate.convertAndSend(channelTopic.getTopic(), messageDto);
		System.out.println("messageDto 발행성공! = " + messageDto);
	}
}
