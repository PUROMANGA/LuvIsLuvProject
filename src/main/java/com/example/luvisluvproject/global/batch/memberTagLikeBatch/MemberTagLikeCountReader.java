package com.example.luvisluvproject.global.batch.memberTagLikeBatch;

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

import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberInteractionLog;
import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberTagLikeCount;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberTagLikeCountReader implements ItemReader<MemberTagLikeCount> {

	private final RedisTemplate<String, String> stringRedisTemplate;
	Map<String, Double> savedTupleForTag = new HashMap<>();
	List<MemberTagLikeCount> memberInteractionLogList = new ArrayList<>();

	private int currentIndex = 0;

	@PostConstruct
	public void setUp() {
		Set<String> membersId = stringRedisTemplate.opsForSet().members("MemberId");

		for (String id : Objects.requireNonNull(membersId)) {
			while (true) {
				ZSetOperations.TypedTuple<String> tuple = stringRedisTemplate.opsForZSet()
					.popMax(id.toString() + "forTag");
				if (tuple == null) {
					break;
				}
				savedTupleForTag.put(tuple.getValue(), tuple.getScore());
			}

			memberInteractionLogList.add(new MemberTagLikeCount(Long.parseLong(id), savedTupleForTag));
		}
	}

	@Override
	public MemberTagLikeCount read() {
		if (currentIndex < memberInteractionLogList.size()) {
			return memberInteractionLogList.get(currentIndex++);
		} else {
			return null;
		}
	}
}
