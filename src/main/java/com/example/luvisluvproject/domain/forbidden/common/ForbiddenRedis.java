package com.example.luvisluvproject.domain.forbidden.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.forbidden.entity.ForbiddenWord;
import com.example.luvisluvproject.domain.forbidden.repository.ForbiddenWordRepository;

@Component
public class ForbiddenRedis implements CommandLineRunner {

	private final ForbiddenWordRepository forbiddenWordRepository;
	private final RedisTemplate<String, String> redisTemplate;

	//토큰템플릿이라 적혀있지만 스트링-스트링 구조의 레디스 템플릿임
	public ForbiddenRedis(ForbiddenWordRepository forbiddenWordRepository, @Qualifier("customStringRedisTemplate")RedisTemplate<String, String> redisTemplate) {
		this.forbiddenWordRepository = forbiddenWordRepository;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void run(String... args) throws Exception {
		if(Boolean.FALSE.equals(redisTemplate.hasKey("banwords"))) {
			List<String> forbiddenWordList = forbiddenWordRepository.findAll()
				.stream()
				.map(ForbiddenWord::getWord)
				.toList();

			int batchSize = 500;

			for(int i = 0; i < forbiddenWordList.size(); i += batchSize) {
				int end = Math.min(forbiddenWordList.size(),i + batchSize);
				List<String> batch = forbiddenWordList.subList(i, end);
				redisTemplate.opsForSet().add("banwords", batch.toArray(new String[0]));
			}
		}

		Set<String> banwords = redisTemplate.opsForSet().members("banwords");
		log.info("금칙어 {}개가 Redis에 로드되었습니다.", banwords.size());
	}
}
