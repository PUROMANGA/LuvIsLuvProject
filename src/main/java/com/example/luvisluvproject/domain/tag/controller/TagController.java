package com.example.luvisluvproject.domain.tag.controller;

import java.util.List;

import com.example.luvisluvproject.domain.tag.dto.SelectTagsRequestDto;
import com.example.luvisluvproject.domain.tag.dto.TagRequestDto;
import com.example.luvisluvproject.domain.tag.dto.TagResponseDto;
import com.example.luvisluvproject.domain.tag.service.TagService;
import com.example.luvisluvproject.global.common.AuthUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

/**
 * 사용자용 태그 기능 API
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class TagController {

	private final TagService tagService;

	/**
	 * 유저가 직접 태그 생성
	 */
	@PostMapping("/tags")
	public ResponseEntity<String> createTag(@RequestBody @Valid List<TagRequestDto> requestDto,
		@AuthenticationPrincipal AuthUser user) {
		tagService.createTag(requestDto, user.getUsername());
		return ResponseEntity.ok("태그가 저장되었습니다.");
	}

	/**
	 * 태그 자동완성 검색 (Elasticsearch prefix 검색 기반)
	 */
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<Slice<TagResponseDto>>> searchTags(
		@RequestParam String keyword,
		Pageable pageable
	) {
		Slice<TagResponseDto> matchedTags = tagService.searchTags(keyword, pageable);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.SEARCH_TAGS_SUCCESS, matchedTags));
	}

	/**
	 * 특정 사용자에게 태그 연결 (선택)
	 */
	@PostMapping("/members/{memberId}")
	public ResponseEntity<ApiResponse<Slice<TagResponseDto>>> assignTagsToMember(
		@PathVariable Long memberId,
		@RequestBody @Valid SelectTagsRequestDto requestDto,
		Pageable pageable
	) {
		Slice<TagResponseDto> selectedTags = tagService.assignTagsToMember(memberId, requestDto.getTagIds(), pageable);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.ASSIGN_TAGS_TO_MEMBER_SUCCESS, selectedTags));
	}

	/**
	 * 사용자가 연결한 태그 목록 조회
	 */
	@GetMapping("/members/{memberId}")
	public ResponseEntity<ApiResponse<Slice<TagResponseDto>>> getTagsOfMember(
		@PathVariable Long memberId,
		Pageable pageable
	) {
		Slice<TagResponseDto> memberTags = tagService.getTagsByMemberId(memberId, pageable);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.GET_MEMBER_TAGS_SUCCESS, memberTags));
	}
}
