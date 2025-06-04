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
@RequestMapping("/admin/tags")
@RequiredArgsConstructor
public class TagAdminController {

	private final TagService tagService;

	/**
	 * 태그 수정
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
	 * 태그 삭제
	 */
	@DeleteMapping("/{tagId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> deleteTag(@PathVariable Long tagId) {
		tagService.deleteTag(tagId);

		// ✅ 간단한 메시지 응답 객체를 컨트롤러 내부에서 생성
		return ResponseEntity.ok(new Object() {
			public final String message = "태그가 성공적으로 삭제되었습니다.";
		});
	}
}
