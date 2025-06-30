package com.example.luvisluvproject.domain.review.controller;

import static org.springframework.data.domain.Sort.Direction.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.domain.review.dto.ReviewCreateRequestDto;
import com.example.luvisluvproject.domain.review.dto.ReviewCreateResponseDto;
import com.example.luvisluvproject.domain.review.dto.ReviewListResponseDto;
import com.example.luvisluvproject.domain.review.dto.ReviewUpdateRequestDto;
import com.example.luvisluvproject.domain.review.dto.ReviewUpdateResponseDto;
import com.example.luvisluvproject.domain.review.service.ReviewService;
import com.example.luvisluvproject.global.common.AuthUser;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/stores/{storeId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	/**
	 * 리뷰를 생성합니다.
	 */
	@PostMapping
	public ResponseEntity<ApiResponse<ReviewCreateResponseDto>> createReview(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long storeId,
		@RequestBody @Valid ReviewCreateRequestDto requestDto) {
		ReviewCreateResponseDto responseDto = reviewService.createReview(storeId, authUser.getUsername(), requestDto);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.CREATE_REVIEW_SUCCESS, responseDto));
	}

	/**
	 * 리뷰를 수정합니다. 작성자 본인만 수정할 수 있습니다.
	 */
	@PutMapping("/{reviewId}")
	public ResponseEntity<ApiResponse<ReviewUpdateResponseDto>> updateReview(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long storeId,
		@PathVariable Long reviewId,
		@RequestBody @Valid ReviewUpdateRequestDto requestDto) {
		ReviewUpdateResponseDto responseDto = reviewService.updateReview(storeId, reviewId, authUser.getUsername(),
			requestDto);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.UPDATE_REVIEW_SUCCESS, responseDto));
	}

	/**
	 * 특정 가게에 작성된 모든 리뷰 목록을 페이징 및 정렬하여 조회합니다.
	 */
	@GetMapping("/reviews")
	public ResponseEntity<ApiResponse<Slice<ReviewListResponseDto>>> getAllReviewsByStore(
		@PathVariable Long storeId,
		@PageableDefault(size = 10, sort = "creatTime", direction = DESC) Pageable pageable) {
		Slice<ReviewListResponseDto> responseDto = reviewService.getAllReviewsByStore(storeId, pageable);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.GET_ALL_REVIEWS_SUCCESS, responseDto));
	}

	/**
	 * 특정 리뷰를 삭제합니다. 작성자 본인만 삭제할 수 있습니다.
	 */
	@DeleteMapping("/{reviewId}")
	public ResponseEntity<ApiResponse<Void>> deleteReview(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long storeId,
		@PathVariable Long reviewId
	) {
		reviewService.deleteReview(storeId, reviewId, authUser.getUsername());
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.DELETE_REVIEW_SUCCESS, null));
	}
}
