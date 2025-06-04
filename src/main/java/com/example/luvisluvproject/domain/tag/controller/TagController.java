package com.example.luvisluvproject.domain.tag.controller;

import com.example.luvisluvproject.domain.tag.dto.TagRequestDto;
import com.example.luvisluvproject.domain.tag.dto.TagResponseDto;
import com.example.luvisluvproject.domain.tag.service.TagService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

	private final TagService tagService;

	/**
	 * 태그 생성 (유저가 직접 등록)
	 */
	@PostMapping
	public ResponseEntity<TagResponseDto> createTag(@RequestBody @Valid TagRequestDto requestDto) {
		TagResponseDto createdTag = tagService.createTag(requestDto);
		return ResponseEntity.status(201).body(createdTag);
	}

	/**
	 * 태그 검색 (부분 매칭: 엘라스틱서치 Edge NGram 기반)
	 */
	@GetMapping("/search")
	public ResponseEntity<List<TagResponseDto>> searchTags(@RequestParam String keyword) {
		List<TagResponseDto> matchedTags = tagService.searchTags(keyword);
		return ResponseEntity.ok(matchedTags);
	}

	/**
	 * 유저가 태그 선택
	 */
	@PostMapping("/members/{memberId}")
	public ResponseEntity<List<TagResponseDto>> selectTagsForMember(
		@PathVariable Long memberId,
		@RequestBody List<Long> tagIds
	) {
		List<TagResponseDto> selectedTags = tagService.assignTagsToMember(memberId, tagIds);
		return ResponseEntity.ok(selectedTags);
	}

	/**
	 * 유저가 선택한 태그 목록 조회
	 */
	@GetMapping("/members/{memberId}")
	public ResponseEntity<List<TagResponseDto>> getTagsOfMember(@PathVariable Long memberId) {
		List<TagResponseDto> memberTags = tagService.getTagsByMemberId(memberId);
		return ResponseEntity.ok(memberTags);
	}
}
