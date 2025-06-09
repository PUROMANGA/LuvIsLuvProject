package com.example.luvisluvproject.domain.tag.controller;

import com.example.luvisluvproject.domain.tag.dto.SelectTagsRequestDto;
import com.example.luvisluvproject.domain.tag.dto.TagRequestDto;
import com.example.luvisluvproject.domain.tag.dto.TagResponseDto;
import com.example.luvisluvproject.domain.tag.service.TagService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TagController {

	private final TagService tagService;

	/**
	 * 유저가 직접 태그 생성
	 */
	@PostMapping("/tags")
	public ResponseEntity<TagResponseDto> createTag(@RequestBody @Valid TagRequestDto requestDto) {
		TagResponseDto createdTag = tagService.createTag(requestDto);
		return ResponseEntity.status(201).body(createdTag);
	}

	/**
	 * 태그 자동완성 검색 (Edge NGram + analyzer)
	 */
	@GetMapping("/tags/search")
	public ResponseEntity<Slice<TagResponseDto>> searchTags(
		@RequestParam String keyword,
		Pageable pageable
	) {
		Slice<TagResponseDto> matchedTags = tagService.searchTags(keyword, pageable);
		return ResponseEntity.ok(matchedTags);
	}

	/**
	 * 유저가 태그 선택
	 */
	@PostMapping("/members/{memberId}/tags")
	public ResponseEntity<Slice<TagResponseDto>> selectTagsForMember(
		@PathVariable Long memberId,
		@RequestBody @Valid SelectTagsRequestDto requestDto,
		Pageable pageable
	) {
		Slice<TagResponseDto> selectedTags = tagService.assignTagsToMember(memberId, requestDto.getTagIds(), pageable);
		return ResponseEntity.ok(selectedTags);
	}

	/**
	 * 유저가 선택한 태그 목록 조회
	 */
	@GetMapping("/members/{memberId}/tags")
	public ResponseEntity<Slice<TagResponseDto>> getTagsOfMember(
		@PathVariable Long memberId,
		Pageable pageable
	) {
		Slice<TagResponseDto> memberTags = tagService.getTagsByMemberId(memberId, pageable);
		return ResponseEntity.ok(memberTags);
	}
}
