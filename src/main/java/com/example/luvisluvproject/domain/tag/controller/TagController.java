package com.example.luvisluvproject.domain.tag.controller;

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
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

	private final TagService tagService;

	/**
	 * 사용자 태그 생성 요청 - Redis에 캐시 후 비동기 저장
	 */
	@PostMapping
	public ResponseEntity<ApiResponse<String>> createTag(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid TagRequestDto requestDto
	) {
		tagService.cacheTagRequest(authUser.getMember(), requestDto);
		return ResponseEntity.ok(ApiResponse.of(
			SuccessCode.CREATE_TAG_REQUEST_SUCCESS,
			"태그가 임시 저장되었습니다. 추후 자동 저장됩니다.")
		);
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
