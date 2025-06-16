package com.example.luvisluvproject.domain.tag.service;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.tag.document.TagDocument;
import com.example.luvisluvproject.domain.tag.dto.CachedTagDto;
import com.example.luvisluvproject.domain.tag.dto.TagRequestDto;
import com.example.luvisluvproject.domain.tag.dto.TagResponseDto;
import com.example.luvisluvproject.domain.tag.entity.MemberTag;
import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.domain.tag.enums.TagCategory;
import com.example.luvisluvproject.domain.tag.repository.MemberTagRepository;
import com.example.luvisluvproject.domain.tag.repository.TagJpaRepository;
import com.example.luvisluvproject.domain.tag.repository.TagSearchRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 태그 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class TagService {

	private final TagJpaRepository tagJpaRepository;
	private final MemberRepository memberRepository;
	private final MemberTagRepository memberTagRepository;
	private final TagSearchRepository tagSearchRepository;

	private final RedisTemplate<String, CachedTagDto> cachedTagRedisTemplate;
	private static final String REDIS_TAG_QUEUE = "pending:tags";

	/**
	 * 유저가 태그 생성 시, 태그 정보를 Redis에 임시 저장
	 */
	public void cacheTagRequest(Member member, TagRequestDto dto) {
		CachedTagDto cached = CachedTagDto.builder()
			.name(dto.getName())
			.category(dto.getCategory())
			.createdByType(dto.getCreatedByType().name())
			.active(dto.isActive())
			.priority(dto.getPriority())
			.memberId(member.getId())
			.build();

		cachedTagRedisTemplate.opsForList().rightPush(REDIS_TAG_QUEUE, cached);
	}

	/**
	 * 태그 정보 수정
	 */
	@Transactional
	public TagResponseDto updateTag(Long tagId, TagRequestDto requestDto) {
		Tag tag = tagJpaRepository.findById(tagId)
			.orElseThrow(() -> new IllegalArgumentException("해당 태그가 존재하지 않습니다."));

		tag.update(Tag.builder()
			.name(requestDto.getName())
			.category(TagCategory.from(requestDto.getCategory()))
			.createdByType(requestDto.getCreatedByType())
			.active(requestDto.isActive())
			.priority(requestDto.getPriority())
			.build());

		return TagResponseDto.from(tag);
	}

	/**
	 * 태그 삭제
	 */
	public void deleteTag(Long tagId) {
		Tag tag = tagJpaRepository.findById(tagId)
			.orElseThrow(() -> new IllegalArgumentException("해당 태그를 찾을 수 없습니다."));
		tagJpaRepository.delete(tag);
	}

	/**
	 * 자동완성 태그 검색 (Elasticsearch prefix 기반)
	 */
	public Slice<TagResponseDto> searchTags(String keyword, Pageable pageable) {
		Slice<TagDocument> documents = tagSearchRepository.searchByName(keyword, pageable);
		List<TagResponseDto> tagDtos = documents.stream()
			.map(TagDocument::toDto)
			.toList();

		return new SliceImpl<>(tagDtos, pageable, documents.hasNext());
	}

	/**
	 * 사용자가 태그를 선택한 경우, 기존 연결 제외하고 새로 연결
	 */
	@Transactional
	public Slice<TagResponseDto> assignTagsToMember(Long memberId, List<Long> tagIds, Pageable pageable) {
		Member member = memberRepository.getReferenceById(memberId);

		Set<Long> existingTagIds = memberTagRepository.findAllByMemberId(memberId).stream()
			.map(mt -> mt.getTag().getId())
			.collect(Collectors.toSet());

		List<MemberTag> toInsert = tagJpaRepository.findAllById(tagIds).stream()
			.filter(tag -> !existingTagIds.contains(tag.getId()))
			.map(tag -> MemberTag.builder()
				.member(member)
				.tag(tag)
				.build())
			.toList();

		memberTagRepository.saveAll(toInsert);

		List<TagResponseDto> dtoList = memberTagRepository.findByMemberId(memberId, pageable).stream()
			.map(mt -> TagResponseDto.from(mt.getTag()))
			.toList();

		boolean hasNext = dtoList.size() > pageable.getPageSize();
		List<TagResponseDto> content = hasNext ? dtoList.subList(0, pageable.getPageSize()) : dtoList;

		return new SliceImpl<>(content, pageable, hasNext);
	}

	/**
	 * 특정 사용자가 선택한 태그 목록 조회
	 */
	public Slice<TagResponseDto> getTagsByMemberId(Long memberId, Pageable pageable) {
		Page<MemberTag> page = memberTagRepository.findByMemberId(memberId, pageable);

		List<TagResponseDto> dtoList = page.getContent().stream()
			.map(mt -> TagResponseDto.from(mt.getTag()))
			.toList();

		return new SliceImpl<>(dtoList, pageable, page.hasNext());
	}

	public TagResponseDto createTag(TagRequestDto requestDto) {
		Tag tag = Tag.builder()
			.name(requestDto.getName())
			.category(TagCategory.from(requestDto.getCategory()))
			.createdByType(requestDto.getCreatedByType())
			.active(requestDto.isActive())
			.priority(requestDto.getPriority())
			.build();

		Tag saved = tagJpaRepository.save(tag);
		return TagResponseDto.from(saved);
	}

}
