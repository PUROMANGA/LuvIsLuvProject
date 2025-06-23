package com.example.luvisluvproject.global.redis;

import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.example.luvisluvproject.domain.chat.dto.MessageDto;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service

public class RedisPublisher {

	private final ChannelTopic channelTopic;
	private final RedisTemplate<String, Object> redisTemplate;
	private final Map<String, String> stompToWebSocketMap;
	private final MemberRepository memberRepository;

	public void publish(MessageDto messageDto) {
		Member member = memberRepository.findById(messageDto.getUserId()).orElseThrow(() -> new CustomRuntimeException(
			ExceptionCode.USER_CANT_FIND));
		String webSocketSessionId = stompToWebSocketMap.get(member.getEmail());
		String value = "유저 : " + member.getName() + " : " + "방 아이디 : " + messageDto.getRoomId();

		if (redisTemplate.opsForSet().members(webSocketSessionId) == null) {
			redisTemplate.opsForSet().add(webSocketSessionId, value);
			System.out.println("메세지 발행");
			redisTemplate.convertAndSend(channelTopic.getTopic(), messageDto);
			System.out.println("messageDto 발행성공! = " + messageDto);
		}

		redisTemplate.convertAndSend(channelTopic.getTopic(), messageDto);
	}
}
