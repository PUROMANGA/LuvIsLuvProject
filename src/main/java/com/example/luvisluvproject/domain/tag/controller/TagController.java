package com.example.luvisluvproject.domain.tag.controller;

import com.example.luvisluvproject.domain.tag.dto.TagRequestDto;
import com.example.luvisluvproject.domain.tag.dto.TagResponseDto;
import com.example.luvisluvproject.domain.tag.service.TagService;
import com.example.luvisluvproject.global.common.AuthUser;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * TagController
 * 일반 사용자용 태그 관련 API
 */
@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

	private final TagService tagService;

	/**
	 * 사용자 태그 생성 요청 - 바로 DB 저장
	 */
	@PostMapping
	public ResponseEntity<ApiResponse<TagResponseDto>> createTag(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid TagRequestDto requestDto
	) {
		TagResponseDto response = tagService.createTag(authUser.getMember(), requestDto);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.CREATE_TAG_REQUEST_SUCCESS, response));
	}

	/**
	 * 자동완성 태그 검색 (Elasticsearch prefix 검색 기반)
	 */
	@GetMapping("/tags/search")
	public ResponseEntity<ApiResponse<Slice<TagResponseDto>>> searchTags(
		@RequestParam String keyword,
		Pageable pageable
	) {
		Slice<TagResponseDto> matchedTags = tagService.searchTags(keyword, pageable);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.SEARCH_TAGS_SUCCESS, matchedTags));
	}

	/**
	 * 사용자가 연결한 태그 목록 조회
	 */
	@GetMapping("/members/{memberId}/tags")
	public ResponseEntity<ApiResponse<Slice<TagResponseDto>>> getTagsOfMember(
		@PathVariable Long memberId,
		Pageable pageable
	) {
		Slice<TagResponseDto> memberTags = tagService.getTagsByMemberId(memberId, pageable);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.GET_MEMBER_TAGS_SUCCESS, memberTags));
	}
}
