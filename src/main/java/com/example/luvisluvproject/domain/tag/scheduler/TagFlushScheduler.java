package com.example.luvisluvproject.domain.tag.scheduler;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.tag.dto.CachedTagDto;
import com.example.luvisluvproject.domain.tag.entity.MemberTag;
import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.domain.tag.enums.TagCategory;
import com.example.luvisluvproject.domain.tag.enums.TagCreatedByType;
import com.example.luvisluvproject.domain.tag.repository.MemberTagRepository;
import com.example.luvisluvproject.domain.tag.repository.TagJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Redis에 임시 저장된 태그를 주기적으로 DB에 저장하는 스케줄러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TagFlushScheduler {

	private final RedisTemplate<String, CachedTagDto> cachedTagRedisTemplate;
	private final TagJpaRepository tagJpaRepository;
	private final MemberRepository memberRepository;
	private final MemberTagRepository memberTagRepository;

	private static final String REDIS_TAG_QUEUE = "pending:tags";

	/**
	 * 30초 간격으로 Redis에서 태그 정보를 읽어 DB에 저장하고 연결 처리
	 */
	@Transactional
	@Scheduled(fixedDelay = 30000)
	public void flushTagsToDatabase() {
		List<CachedTagDto> cachedList = cachedTagRedisTemplate.opsForList().range(REDIS_TAG_QUEUE, 0, -1);
		if (cachedList == null || cachedList.isEmpty()) return;

		int savedCount = 0;

		for (CachedTagDto cached : cachedList) {
			// 중복 방지
			if (tagJpaRepository.findByName(cached.getName()).isPresent()) continue;

			Tag tag = Tag.builder()
				.name(cached.getName())
				.category(TagCategory.from(cached.getCategory()))
				.createdByType(TagCreatedByType.valueOf(cached.getCreatedByType()))
				.active(cached.isActive())
				.priority(cached.getPriority())
				.build();

			Tag saved = tagJpaRepository.save(tag);

			Member member = memberRepository.getReferenceById(cached.getMemberId());
			MemberTag link = MemberTag.builder().member(member).tag(saved).build();
			memberTagRepository.save(link);

			savedCount++;
		}

		cachedTagRedisTemplate.delete(REDIS_TAG_QUEUE);
		log.info("[Scheduled] 태그 {}개 저장 및 연결 완료", savedCount);
	}
}
