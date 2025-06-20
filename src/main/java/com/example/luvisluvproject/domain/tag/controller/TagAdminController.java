package com.example.luvisluvproject.domain.tag.controller;

import com.example.luvisluvproject.domain.tag.dto.TagRequestDto;
import com.example.luvisluvproject.domain.tag.dto.TagResponseDto;
import com.example.luvisluvproject.domain.tag.service.TagService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

/**
 * TagAdminController
 * 관리자 전용 태그 관리 API 컨트롤러
 */
@RestController
@RequestMapping("/admin/tags")
@RequiredArgsConstructor
public class TagAdminController {

	private final TagService tagService;

	/**
	 * 태그 수정 (관리자 전용)
	 *
	 * @param tagId      수정할 태그 ID
	 * @param requestDto 태그 수정 요청 정보
	 * @return 수정된 태그 응답 DTO
	 */
	@PatchMapping("/{tagId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<TagResponseDto>> updateTag(
		@PathVariable Long tagId,
		@RequestBody @Valid TagRequestDto requestDto
	) {
		TagResponseDto updatedTag = tagService.updateTag(tagId, requestDto);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.UPDATE_TAG_SUCCESS, updatedTag));
	}

	/**
	 * 태그 삭제 (관리자 전용)
	 *
	 * @param tagId 삭제할 태그 ID
	 * @return 삭제 성공 응답 (본문 없이 상태 코드만)
	 */
	@DeleteMapping("/{tagId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<Void>> deleteTag(@PathVariable Long tagId) {
		tagService.deleteTag(tagId);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.DELETE_TAG_SUCCESS, null));
	}
}
