package com.example.luvisluvproject.domain.review.controller;

import java.util.List;

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

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.review.dto.ReviewCreateRequestDto;
import com.example.luvisluvproject.domain.review.dto.ReviewCreateResponseDto;
import com.example.luvisluvproject.domain.review.dto.ReviewDetailResponseDto;
import com.example.luvisluvproject.domain.review.dto.ReviewListResponseDto;
import com.example.luvisluvproject.domain.review.dto.ReviewUpdateRequestDto;
import com.example.luvisluvproject.domain.review.dto.ReviewUpdateResponseDto;
import com.example.luvisluvproject.domain.review.service.ReviewService;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/stores/{storeId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	/**
	 * 새로운 리뷰를 작성합니다.
	 * @param loginMember 인증된 사용자(Member) 정보
	 * @param storeId 리뷰를 작성할 가게의 ID
	 * @param requestDto 리뷰 작성 요청 데이터 (평점, 내용 등)
	 * @return 작성된 리뷰 정보와 성공 메시지를 포함한 ApiResponse를 ResponseEntity로 반환
	 */
	@PostMapping
	public ResponseEntity<ApiResponse<ReviewCreateResponseDto>> createReview(
		@AuthenticationPrincipal Member loginMember,
		@PathVariable Long storeId,
		@RequestBody ReviewCreateRequestDto requestDto
	) {
		ReviewCreateResponseDto responseDto = reviewService.createReview(storeId, loginMember, requestDto);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.CREATE_REVIEW_SUCCESS, responseDto));
	}

	/**
	 * 특정 리뷰를 수정합니다.
	 * @param loginMember 인증된 사용자(Member) 정보
	 * @param storeId 수정할 리뷰가 속한 가게의 ID
	 * @param reviewId 수정할 리뷰의 ID
	 * @param requestDto 리뷰 수정 요청 데이터 (수정할 내용)
	 * @return 수정된 리뷰 정보와 성공 메시지를 포함한 ApiResponse를 ResponseEntity로 반환
	 */
	@PutMapping("/{reviewId}")
	public ResponseEntity<ApiResponse<ReviewUpdateResponseDto>> updateReview(
		@AuthenticationPrincipal Member loginMember,
		@PathVariable Long storeId,
		@PathVariable Long reviewId,
		@RequestBody ReviewUpdateRequestDto requestDto
	) {
		ReviewUpdateResponseDto responseDto = reviewService.updateReview(storeId, reviewId, loginMember.getId(),
			requestDto);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.UPDATE_REVIEW_SUCCESS, responseDto));
	}

	/**
	 * 특정 리뷰의 상세 정보를 조회합니다.
	 * @param storeId 조회할 리뷰가 속한 가게의 ID
	 * @param reviewId 조회할 리뷰의 ID
	 * @return 리뷰 상세 정보와 성공 메시지를 포함한 ApiResponse를 ResponseEntity로 반환
	 */
	@GetMapping("/{reviewId}")
	public ResponseEntity<ApiResponse<ReviewDetailResponseDto>> getReviewDetail(
		@PathVariable Long storeId,
		@PathVariable Long reviewId
	) {
		ReviewDetailResponseDto responseDto = reviewService.getReviewDetail(storeId, reviewId);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.GET_REVIEW_SUCCESS, responseDto));
	}

	/**
	 * 특정 가게에 작성된 모든 리뷰 목록을 조회합니다.
	 * @param storeId 조회할 리뷰가 속한 가게의 ID
	 * @return 리뷰 목록과 성공 메시지를 포함한 ApiResponse를 ResponseEntity로 반환
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<List<ReviewListResponseDto>>> getAllReviewsByStore(
		@PathVariable Long storeId
	) {
		List<ReviewListResponseDto> responseDto = reviewService.getAllReviewsByStore(storeId);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.GET_ALL_REVIEWS_SUCCESS, responseDto));
	}

	/**
	 * 특정 리뷰를 삭제합니다.
	 * @param loginMember 인증된 사용자(Member) 정보
	 * @param storeId 삭제할 리뷰가 속한 가게의 ID
	 * @param reviewId 삭제할 리뷰의 ID
	 * @return 삭제 성공 메시지를 포함한 ApiResponse를 ResponseEntity로 반환
	 */
	@DeleteMapping("/{reviewId}")
	public ResponseEntity<ApiResponse<Void>> deleteReview(
		@AuthenticationPrincipal Member loginMember,
		@PathVariable Long storeId,
		@PathVariable Long reviewId
	) {
		reviewService.deleteReview(storeId, reviewId, loginMember.getId());
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.DELETE_REVIEW_SUCCESS, null));
	}
}
