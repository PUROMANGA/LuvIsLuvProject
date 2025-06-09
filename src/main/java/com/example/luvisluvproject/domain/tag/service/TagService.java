package com.example.luvisluvproject.domain.tag.service;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.tag.document.TagDocument;
import com.example.luvisluvproject.domain.tag.dto.TagRequestDto;
import com.example.luvisluvproject.domain.tag.dto.TagResponseDto;
import com.example.luvisluvproject.domain.tag.entity.MemberTag;
import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.domain.tag.repository.MemberTagRepository;
import com.example.luvisluvproject.domain.tag.repository.TagJpaRepository;
import com.example.luvisluvproject.domain.tag.repository.TagSearchRepository;
import com.example.luvisluvproject.domain.tag.enums.TagCategory;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
	public TagResponseDto updateTag(Long tagId, TagRequestDto requestDto) {
		Tag tag = tagJpaRepository.findById(tagId)
			.orElseThrow(() -> new IllegalArgumentException("해당 태그가 존재하지 않습니다."));

		tag.update(Tag.builder()
			.name(requestDto.getName())
			.category(TagCategory.from(requestDto.getCategory()))
			.createdByType(requestDto.getCreatedByType())
			.active(requestDto.isActive())
			.priority(requestDto.getPriority())
			.build()
		);

		Tag updated = tagJpaRepository.save(tag);
		return TagResponseDto.from(updated);
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
			.map(tag -> TagResponseDto.builder()
				.id(tag.getId())
				.name(tag.getName())
				.category(TagCategory.from(tag.getCategory()))
				.createdByType(tag.getCreatedByType())
				.active(tag.isActive())
				.priority(tag.getPriority())
				.build())
			.collect(Collectors.toList());

		return new SliceImpl<>(tagDtos, pageable, documents.hasNext());
	}

	/**
	 * 사용자에게 태그 할당 - Slice 반환
	 */
	@Transactional
	public Slice<TagResponseDto> assignTagsToMember(Long memberId, List<Long> tagIds, Pageable pageable) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

		memberTagRepository.deleteByMemberId(memberId);

		List<Tag> tags = tagJpaRepository.findAllById(tagIds);
		List<MemberTag> memberTags = tags.stream()
			.map(tag -> MemberTag.builder()
				.member(member)
				.tag(tag)
				.build())
			.collect(Collectors.toList());

		memberTagRepository.saveAll(memberTags);

		List<TagResponseDto> dtoList = tags.stream()
			.map(TagResponseDto::from)
			.collect(Collectors.toList());

		boolean hasNext = dtoList.size() > pageable.getPageSize();
		List<TagResponseDto> content = hasNext ? dtoList.subList(0, pageable.getPageSize()) : dtoList;

		return new SliceImpl<>(content, pageable, hasNext);
	}

	/**
	 * 사용자 ID로 태그 조회 - Slice 반환
	 */
	public Slice<TagResponseDto> getTagsByMemberId(Long memberId, Pageable pageable) {
		List<MemberTag> memberTags = memberTagRepository.findAllByMemberId(memberId);

		List<TagResponseDto> dtoList = memberTags.stream()
			.map(mt -> TagResponseDto.from(mt.getTag()))
			.collect(Collectors.toList());

		boolean hasNext = dtoList.size() > pageable.getPageSize();
		List<TagResponseDto> content = hasNext ? dtoList.subList(0, pageable.getPageSize()) : dtoList;

		return new SliceImpl<>(content, pageable, hasNext);
	}
}
