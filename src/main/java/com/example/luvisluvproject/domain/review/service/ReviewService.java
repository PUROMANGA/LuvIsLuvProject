package com.example.luvisluvproject.domain.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.review.dto.ReviewCreateRequestDto;
import com.example.luvisluvproject.domain.review.dto.ReviewCreateResponseDto;
import com.example.luvisluvproject.domain.review.dto.ReviewDetailResponseDto;
import com.example.luvisluvproject.domain.review.dto.ReviewListResponseDto;
import com.example.luvisluvproject.domain.review.dto.ReviewUpdateRequestDto;
import com.example.luvisluvproject.domain.review.dto.ReviewUpdateResponseDto;
import com.example.luvisluvproject.domain.review.entity.Review;
import com.example.luvisluvproject.domain.review.repository.ReviewRepository;
import com.example.luvisluvproject.domain.store.entity.Store;
import com.example.luvisluvproject.domain.store.repository.StoreRepository;
import com.example.luvisluvproject.global.common.AuthUser;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final StoreRepository storeRepository;

	/**
	 * 주어진 가게 ID와 인증된 사용자 정보를 바탕으로 새로운 리뷰를 생성합니다.
	 * 가게가 존재하지 않으면 예외가 발생하며,
	 * 리뷰는 작성자의 회원 정보와 요청된 평점 및 내용을 포함하여 저장됩니다.
	 *
	 * @param storeId    리뷰를 작성할 대상 가게의 ID
	 * @param authUser   인증된 사용자 정보를 담고 있는 객체 (현재 로그인한 사용자)
	 * @param requestDto 리뷰 작성 요청 데이터 (평점, 내용 포함)
	 * @return 생성된 리뷰의 ID와 생성 시간을 담은 {@link ReviewCreateResponseDto} 객체
	 * @throws CustomRuntimeException {@link ExceptionCode#STORE_NOT_FOUND} 가게가 존재하지 않을 경우 발생
	 */
	@Transactional
	public ReviewCreateResponseDto createReview(Long storeId, AuthUser authUser, ReviewCreateRequestDto requestDto) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.STORE_NOT_FOUND));

		Member member = authUser.getMember(); // 인증된 사용자 정보 추출
		Review review = new Review(store, member, requestDto.getRating(), requestDto.getContent());
		Review savedReview = reviewRepository.save(review);

		return new ReviewCreateResponseDto(savedReview.getId(), savedReview.getCreatTime());
	}

	/**
	 * 리뷰를 수정합니다.
	 * 해당 리뷰가 지정된 가게에 속하고, 요청자가 작성자인 경우에만 수정이 가능합니다.
	 *
	 * @param storeId 리뷰가 속한 가게의 ID
	 * @param reviewId 수정할 리뷰의 ID
	 * @param authUser 인증된 사용자 정보
	 * @param requestDto 수정할 평점과 내용이 담긴 DTO
	 * @return 수정된 리뷰 ID와 수정 시간 정보를 담은 응답 DTO
	 * @throws CustomRuntimeException
	 * 리뷰가 존재하지 않거나, 가게-리뷰 불일치 또는 작성자가 아닌 경우 예외 발생
	 */
	@Transactional
	public ReviewUpdateResponseDto updateReview(Long storeId, Long reviewId,
		AuthUser authUser,
		ReviewUpdateRequestDto requestDto) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.REVIEW_NOT_FOUND));

		if (!review.getStoreId().getId().equals(storeId)) {
			throw new CustomRuntimeException(ExceptionCode.STORE_REVIEW_MISMATCH);
		}

		if (!review.getMemberId().getId().equals(authUser.getMember().getId())) {
			throw new CustomRuntimeException(ExceptionCode.EDIT_REVIEW_WRITER_ONLY);
		}

		review.updateRating(requestDto.getRating());
		review.updateContent(requestDto.getContent());

		return new ReviewUpdateResponseDto(review.getId(), review.getModifiedTime()
		);
	}

	/**
	 * 특정 리뷰의 상세 정보를 조회합니다.
	 * 리뷰가 지정된 가게에 속하는지 확인한 후, 리뷰 정보와 해당 가게의 평균 평점을 반환합니다.
	 * 평균 평점은 소수 첫째 자리까지 반올림됩니다.
	 *
	 * @param storeId   리뷰가 속한 가게의 ID
	 * @param reviewId  조회할 리뷰의 ID
	 * @return 리뷰 상세 정보와 가게의 평균 평점을 담은 응답 DTO
	 * @throws CustomRuntimeException 리뷰가 존재하지 않거나, 가게-리뷰 불일치 시 예외 발생
	 */
	@Transactional(readOnly = true)
	public ReviewDetailResponseDto getReviewDetail(Long storeId, Long reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.REVIEW_NOT_FOUND));

		if (!review.getStoreId().getId().equals(storeId)) {
			throw new CustomRuntimeException(ExceptionCode.STORE_REVIEW_MISMATCH);
		}

		String storeName = review.getStoreId().getName();
		Store store = review.getStoreId();

		// 평균 평점 조회 (소수 첫째 자리 반올림)
		Double averageRatingWrapper = reviewRepository.findAverageRatingByStore(store);
		double averageRating = averageRatingWrapper != null
			? Math.round(averageRatingWrapper * 10.0) / 10.0 : 0.0;

		return new ReviewDetailResponseDto(
			review.getId(),
			storeName,
			averageRating,
			review.getRating(),
			review.getContent(),
			review.getCreatTime()
		);
	}

	/**
	 * 특정 가게에 작성된 모든 리뷰를 페이징 처리하여 조회합니다.
	 * @param storeId 조회할 가게의 ID
	 * @param pageable 페이지 번호, 크기, 정렬 정보를 담은 Pageable 객체
	 * @return 페이지 정보가 포함된 ReviewListResponseDto의 Page 객체
	 * @throws CustomRuntimeException 해당 가게가 존재하지 않을 경우 발생
	 */
	@Transactional(readOnly = true)
	public Page<ReviewListResponseDto> getAllReviewsByStore(Long storeId, Pageable pageable) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.STORE_NOT_FOUND));

		Page<Review> reviewsPage = reviewRepository.findByStoreId(store, pageable);

		return reviewsPage.map(review -> new ReviewListResponseDto(
			review.getId(),
			review.getRating(),
			review.getContent(),
			review.getCreatTime()
		));
	}

	/**
	 * 리뷰를 삭제합니다.
	 * 작성자 본인만 삭제할 수 있으며,
	 * 리뷰가 없거나 가게와 리뷰가 맞지 않거나 작성자가 아니면 예외가 발생합니다.
	 *
	 * @param storeId   리뷰가 속한 가게 ID
	 * @param reviewId  삭제할 리뷰 ID
	 * @param authUser  인증된 사용자 정보
	 * @throws CustomRuntimeException 예외 상황 발생 시
	 */
	@Transactional
	public void deleteReview(Long storeId, Long reviewId, AuthUser authUser) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.REVIEW_NOT_FOUND));

		if (!review.getStoreId().getId().equals(storeId)) {
			throw new CustomRuntimeException(ExceptionCode.STORE_REVIEW_MISMATCH);
		}

		if (!review.getMemberId().getId().equals(authUser.getMember().getId())) {
			throw new CustomRuntimeException(ExceptionCode.DELETE_REVIEW_WRITER_ONLY);
		}
		reviewRepository.delete(review);
	}
}
