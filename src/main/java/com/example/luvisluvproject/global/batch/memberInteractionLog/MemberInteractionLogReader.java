package com.example.luvisluvproject.global.batch.memberInteractionLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberInteractionLog;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberInteractionLogReader implements ItemReader<MemberInteractionLog> {

	private final RedisTemplate<String, String> customStringRedisTemplate;
	Map<String, Double> savedTuple = new HashMap<>();
	List<MemberInteractionLog> memberInteractionLogList = new ArrayList<>();

	private int currentIndex = 0;

	@PostConstruct
	public void setUp() {
		Set<String> membersId = customStringRedisTemplate.opsForSet().members("MemberId");

		for (String id : Objects.requireNonNull(membersId)) {
			while (true) {
				ZSetOperations.TypedTuple<String> tuple = customStringRedisTemplate.opsForZSet().popMax(id.toString());
				if (tuple == null) {
					break;
				}
				savedTuple.put(tuple.getValue(), tuple.getScore());
			}

			memberInteractionLogList.add(new MemberInteractionLog(Long.parseLong(id), savedTuple));
		}
	}

	@Override
	public MemberInteractionLog read() {
		if (currentIndex < memberInteractionLogList.size()) {
			return memberInteractionLogList.get(currentIndex++);
		} else {
			return null;
		}
	}
}
