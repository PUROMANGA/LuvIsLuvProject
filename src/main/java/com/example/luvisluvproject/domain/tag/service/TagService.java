package com.example.luvisluvproject.domain.tag.service;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.tag.document.TagDocument;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

	private final TagJpaRepository tagJpaRepository;
	private final MemberRepository memberRepository;
	private final MemberTagRepository memberTagRepository;
	private final TagSearchRepository tagSearchRepository;

	/**
	 * 태그 등록
	 */
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

	/**
	 * 태그 수정
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

		// save 호출 없이 트랜잭션 내 변경 자동 반영
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
	 * 자동완성 검색 - Slice 적용
	 */
	public Slice<TagResponseDto> searchTags(String keyword, Pageable pageable) {
		Slice<TagDocument> documents = tagSearchRepository.searchByName(keyword, pageable);

		List<TagResponseDto> tagDtos = documents.stream()
			.map(TagDocument::toDto)
			.toList();

		return new SliceImpl<>(tagDtos, pageable, documents.hasNext());
	}

	/**
	 * 사용자에게 태그 할당 (차이만 반영) - Slice 반환
	 */
	@Transactional
	public Slice<TagResponseDto> assignTagsToMember(Long memberId, List<Long> tagIds, Pageable pageable) {
		Member member = memberRepository.getReferenceById(memberId);
		List<MemberTag> existing = memberTagRepository.findAllByMemberId(memberId);
		Set<Long> existingTagIds = existing.stream()
			.map(mt -> mt.getTag().getId())
			.collect(Collectors.toSet());

		List<Long> toAdd = tagIds.stream()
			.filter(id -> !existingTagIds.contains(id))
			.toList();

		List<MemberTag> toInsert = tagJpaRepository.findAllById(toAdd).stream()
			.map(tag -> MemberTag.builder()
				.member(member)
				.tag(tag)
				.build())
			.toList();

		memberTagRepository.saveAll(toInsert);

		List<TagResponseDto> dtoList = memberTagRepository.findAllByMemberId(memberId).stream()
			.map(mt -> TagResponseDto.from(mt.getTag()))
			.toList();

		boolean hasNext = dtoList.size() > pageable.getPageSize();
		List<TagResponseDto> content = hasNext ? dtoList.subList(0, pageable.getPageSize()) : dtoList;

		return new SliceImpl<>(content, pageable, hasNext);
	}

	/**
	 * 사용자 ID로 태그 조회 - Slice 적용
	 */
	public Slice<TagResponseDto> getTagsByMemberId(Long memberId, Pageable pageable) {
		Page<MemberTag> page = memberTagRepository.findByMemberId(memberId, pageable);

		List<TagResponseDto> dtoList = page.getContent().stream()
			.map(mt -> TagResponseDto.from(mt.getTag()))
			.toList();

		return new SliceImpl<>(dtoList, pageable, page.hasNext());
	}
}
