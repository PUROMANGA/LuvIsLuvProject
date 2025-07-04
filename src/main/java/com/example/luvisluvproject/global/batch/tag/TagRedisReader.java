package com.example.luvisluvproject.global.batch.tag;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.tag.entity.Tag;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TagRedisReader implements ItemReader<Tag> {

	private final RedisTemplate<String, Tag> tagRedisTemplate;
	@Override
	public Tag read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		try {
			return tagRedisTemplate.opsForSet().pop("Tag");
		} catch (Exception e) {
			throw new RuntimeException("배치 작업 리딩 중 오류 발생", e);
		}
	}
}
