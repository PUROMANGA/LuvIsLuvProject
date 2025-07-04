package com.example.luvisluvproject.flow;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import com.example.luvisluvproject.domain.review.dto.ReviewCreateRequestDto;
import com.example.luvisluvproject.domain.review.dto.ReviewCreateResponseDto;
import com.example.luvisluvproject.domain.review.dto.ReviewListResponseDto;
import com.example.luvisluvproject.domain.review.dto.ReviewUpdateRequestDto;
import com.example.luvisluvproject.domain.review.dto.ReviewUpdateResponseDto;
import com.example.luvisluvproject.domain.review.service.ReviewService;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class ReviewFlowTest {

	@Autowired
	private ReviewService reviewService;

	@Test
	@DisplayName("리뷰 작성 테스트")
	void 리뷰_작성_테스트() {

		String email = "user101@example.com";
		ReviewCreateRequestDto reviewCreateRequestDto = new ReviewCreateRequestDto(
			3, "내용"
		);

		ReviewCreateResponseDto reviewCreateResponseDto = reviewService.createReview(1L, email, reviewCreateRequestDto);
		ApiResponse<ReviewCreateResponseDto> apiResponse = ApiResponse.of(SuccessCode.CREATE_REVIEW_SUCCESS, reviewCreateResponseDto);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("리뷰 작성 성공");
	}

	@Test
	@DisplayName("리뷰 수정 테스트")
	void 리뷰_수정_테스트() {

		String email = "user101@example.com";
		ReviewUpdateRequestDto reviewUpdateRequestDto = new ReviewUpdateRequestDto("수정된 내용");

		ReviewUpdateResponseDto reviewUpdateResponseDto = reviewService.updateReview(1L, 1L, email, reviewUpdateRequestDto);
		ApiResponse<ReviewUpdateResponseDto> apiResponse = ApiResponse.of(SuccessCode.UPDATE_REVIEW_SUCCESS, reviewUpdateResponseDto);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("리뷰 수정 성공");
	}

	@Test
	@DisplayName("리뷰 페이징 조회 테스트")
	void 리뷰_페이징_조회_테스트() {
		Pageable pageable = PageRequest.of(0,10, Sort.by(Sort.Direction.DESC, "creatTime"));
		Slice<ReviewListResponseDto> reviewListResponseDtos = reviewService.getAllReviewsByStore(1L, pageable);
		ApiResponse<Slice<ReviewListResponseDto>> apiResponse = ApiResponse.of(SuccessCode.GET_ALL_REVIEWS_SUCCESS, reviewListResponseDtos);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("리뷰 전체 조회 성공");
	}

	@Test
	@DisplayName("리뷰 삭제 테스트")
	void 리뷰_삭제_테스트() {
		String email = "user101@example.com";
		reviewService.deleteReview(1L, 1L, email);
		ApiResponse<Void> apiResponse = ApiResponse.of(SuccessCode.DELETE_REVIEW_SUCCESS, null);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("리뷰 삭제 성공");
	}
}
