// package com.example.luvisluvproject.domain.tag.controller;
//
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.PatchMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import com.example.luvisluvproject.domain.tag.dto.TagRequestDto;
// import com.example.luvisluvproject.domain.tag.dto.TagResponseDto;
// import com.example.luvisluvproject.domain.tag.service.TagService;
// import com.example.luvisluvproject.global.success.ApiResponse;
// import com.example.luvisluvproject.global.success.SuccessCode;
//
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
//
// /**
//  * 관리자 전용 태그 관리 API
//  */
// @RestController
// @RequestMapping("/admin/tags")
// @RequiredArgsConstructor
// public class TagAdminController {
//
// 	private final TagService tagService;
//
// 	/**
// 	 * 태그 수정 (관리자 권한 필요)
// 	 */
// 	@PatchMapping("/{tagId}")
// 	@PreAuthorize("hasRole('ADMIN')")
// 	public ResponseEntity<ApiResponse<TagResponseDto>> updateTag(
// 		@PathVariable Long tagId,
// 		@RequestBody @Valid TagRequestDto requestDto
// 	) {
// 		TagResponseDto updatedTag = tagService.updateTag(tagId, requestDto);
// 		return ResponseEntity.ok(ApiResponse.of(SuccessCode.UPDATE_TAG_SUCCESS, updatedTag));
// 	}
//
// 	/**
// 	 * 태그 삭제 (관리자 권한 필요)
// 	 */
// 	@DeleteMapping("/{tagId}")
// 	@PreAuthorize("hasRole('ADMIN')")
// 	public ResponseEntity<ApiResponse<String>> deleteTag(@PathVariable Long tagId) {
// 		tagService.deleteTag(tagId);
// 		return ResponseEntity.ok(ApiResponse.of(SuccessCode.DELETE_TAG_SUCCESS, "태그가 삭제되었습니다."));
// 	}
// }
