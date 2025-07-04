package com.example.luvisluvproject.global.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.example.luvisluvproject.domain.chat.dto.MessageDto;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.memberInteractionLog.repository.MemberInteractionLogRepository;
import com.example.luvisluvproject.domain.notify.dto.NotifyDto;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

@Service

public class RedisPublisher {

	private final ChannelTopic channelTopic;
	private final ChannelTopic notifyChannelTopic;
	private final RedisTemplate<String, Object> redisTemplate;
	private final RedisTemplate<String, String> customStringRedisTemplate;
	private final MemberRepository memberRepository;

	public RedisPublisher(ChannelTopic channelTopic, ChannelTopic notifyChannelTopic,
		RedisTemplate<String, Object> redisTemplate,
		@Qualifier("customStringRedisTemplate") RedisTemplate<String, String> customStringRedisTemplate,
		MemberRepository memberRepository) {
		this.channelTopic = channelTopic;
		this.notifyChannelTopic = notifyChannelTopic;
		this.redisTemplate = redisTemplate;
		this.customStringRedisTemplate = customStringRedisTemplate;
		this.memberRepository = memberRepository;
	}

	public void publish(MessageDto messageDto) {
		Member member = memberRepository.findById(messageDto.getUserId()).orElseThrow(() -> new CustomRuntimeException(
			ExceptionCode.USER_CANT_FIND));
		String webSocketSessionId = customStringRedisTemplate.opsForValue().get(member.getEmail());
		String value = "유저 : " + member.getName() + " : " + "방 아이디 : " + messageDto.getRoomId();

		if (redisTemplate.opsForSet().members(webSocketSessionId) == null) {
			redisTemplate.opsForSet().add(webSocketSessionId, value);
			System.out.println("메세지 발행");
			redisTemplate.convertAndSend(channelTopic.getTopic(), messageDto);
		}

		redisTemplate.convertAndSend(channelTopic.getTopic(), messageDto);
		customStringRedisTemplate.opsForZSet().incrementScore(member.getId().toString(), "MessageCount", 1);
	}

	public void publishNotify(NotifyDto notifyDto) {
		redisTemplate.convertAndSend(notifyChannelTopic.getTopic(), notifyDto);
	}
}
