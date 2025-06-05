package com.example.luvisluvproject.domain.review.service;

import java.util.List;
import java.util.stream.Collectors;

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
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final StoreRepository storeRepository;

	/**
	 * 새로운 리뷰를 생성합니다.
	 * @param storeId 리뷰가 작성될 가게의 ID
	 * @param member 리뷰 작성자(Member) 엔티티
	 * @param requestDto 리뷰 생성 요청 데이터 (평점, 내용)
	 * @return 생성된 리뷰 정보와 메시지를 포함한 ReviewCreateResponseDto 객체
	 * @throws CustomRuntimeException 가게가 존재하지 않을 경우 발생
	 */
	@Transactional
	public ReviewCreateResponseDto createReview(Long storeId, Member member, ReviewCreateRequestDto requestDto) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.STORE_NOT_FOUND));

		Review review = new Review(store, member, requestDto.getRating(), requestDto.getContent());
		Review savedReview = reviewRepository.save(review);

		return new ReviewCreateResponseDto(
			savedReview.getId(),
			savedReview.getCreatedAt(),
			"후기를 작성했어요."
		);
	}

	/**
	 * 기존 리뷰를 수정합니다. 작성자인지 확인 후 수정 가능합니다.
	 * @param storeId 리뷰가 속한 가게의 ID
	 * @param reviewId 수정할 리뷰의 ID
	 * @param loginMemberId 리뷰 작성자(Member) ID
	 * @param requestDto 리뷰 수정 요청 데이터 (수정할 내용)
	 * @return 수정된 리뷰 정보와 메시지를 포함한 ReviewUpdateResponseDto 객체
	 * @throws CustomRuntimeException 리뷰가 없거나 가게/작성자 확인 실패 시 발생
	 */
	@Transactional
	public ReviewUpdateResponseDto updateReview(Long storeId, Long reviewId,
		Long loginMemberId,
		ReviewUpdateRequestDto requestDto) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.REVIEW_NOT_FOUND));

		if (!review.getStoreId().getId().equals(storeId)) {
			throw new CustomRuntimeException(ExceptionCode.STORE_REVIEW_MISMATCH);
		}

		if (!review.getMemberId().getId().equals(loginMemberId)) {
			throw new CustomRuntimeException(ExceptionCode.EDIT_REVIEW_WRITER_ONLY);
		}

		review.updateContent(requestDto.getContent());

		return new ReviewUpdateResponseDto(
			review.getId(),
			review.getModifiedTime(),
			"후기를 새롭게 수정했습니다."
		);
	}

	/**
	 * 특정 리뷰의 상세 정보를 조회합니다.
	 * @param storeId 리뷰가 속한 가게의 ID
	 * @param reviewId 조회할 리뷰의 ID
	 * @return 조회된 리뷰 상세 정보를 담은 ReviewDetailResponseDto 객체
	 * @throws CustomRuntimeException 리뷰가 존재하지 않거나, 리뷰가 가게와 매칭되지 않을 경우 발생
	 */
	@Transactional(readOnly = true)
	public ReviewDetailResponseDto getReviewDetail(Long storeId, Long reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.REVIEW_NOT_FOUND));

		if (!review.getStoreId().getId().equals(storeId)) {
			throw new CustomRuntimeException(ExceptionCode.STORE_REVIEW_MISMATCH);
		}

		return new ReviewDetailResponseDto(
			review.getId(),
			storeId,
			review.getRating(),
			review.getContent(),
			review.getCreatedAt()
		);
	}

	/**
	 * 특정 가게에 작성된 모든 리뷰를 조회합니다.
	 * @param storeId 조회할 가게의 ID
	 * @return 해당 가게에 작성된 리뷰 목록을 ReviewListResponseDto 리스트로 반환
	 * @throws CustomRuntimeException 가게가 존재하지 않을 경우 발생
	 */
	@Transactional(readOnly = true)
	public List<ReviewListResponseDto> getAllReviewsByStore(Long storeId) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.STORE_NOT_FOUND));

		List<Review> reviews = reviewRepository.findByStoreId(store);

		return reviews.stream()
			.map(review -> new ReviewListResponseDto(
				review.getId(),
				review.getRating(),
				review.getContent(),
				review.getCreatedAt()
			))
			.collect(Collectors.toList());
	}

	/**
	 * 리뷰를 삭제합니다.
	 * @param storeId       가게 ID
	 * @param reviewId      리뷰 ID
	 * @param loginMemberId 요청자 회원 ID
	 * @throws CustomRuntimeException 리뷰가 없거나 가게-리뷰 불일치, 작성자가 아니면 발생
	 */
	@Transactional
	public void deleteReview(Long storeId, Long reviewId, Long loginMemberId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.REVIEW_NOT_FOUND));

		if (!review.getStoreId().getId().equals(storeId)) {
			throw new CustomRuntimeException(ExceptionCode.STORE_REVIEW_MISMATCH);
		}

		if (!review.getMemberId().getId().equals(loginMemberId)) {
			throw new CustomRuntimeException(ExceptionCode.DELETE_REVIEW_WRITER_ONLY);
		}
		reviewRepository.delete(review);
	}
}
