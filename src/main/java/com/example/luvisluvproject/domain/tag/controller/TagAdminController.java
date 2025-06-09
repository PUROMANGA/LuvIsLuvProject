package com.example.luvisluvproject.domain.tag.controller;

import com.example.luvisluvproject.domain.tag.dto.TagRequestDto;
import com.example.luvisluvproject.domain.tag.dto.TagResponseDto;
import com.example.luvisluvproject.domain.tag.service.TagService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagAdminController {

	private final TagService tagService;

	/**
	 * 태그 수정 - 관리자만 가능
	 */
	@PatchMapping("/{tagId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<TagResponseDto> updateTag(
		@PathVariable Long tagId,
		@RequestBody @Valid TagRequestDto requestDto
	) {
		TagResponseDto updatedTag = tagService.updateTag(tagId, requestDto);
		return ResponseEntity.ok(updatedTag);
	}

	/**
	 * 태그 삭제 - 관리자만 가능
	 */
	@DeleteMapping("/{tagId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> deleteTag(@PathVariable Long tagId) {
		tagService.deleteTag(tagId);
		return ResponseEntity.ok("태그가 성공적으로 삭제되었습니다.");
	}
}
