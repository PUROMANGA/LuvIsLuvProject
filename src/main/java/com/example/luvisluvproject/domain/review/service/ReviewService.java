package com.example.luvisluvproject.domain.review.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.review.dto.ReviewCreateRequestDto;
import com.example.luvisluvproject.domain.review.dto.ReviewCreateResponseDto;
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
	private final MemberRepository memberRepository;

	/**
	 * 주어진 가게 ID와 인증된 사용자 정보를 바탕으로 새로운 리뷰를 생성합니다.
	 * 가게가 존재하지 않으면 예외가 발생하며,
	 * 리뷰는 작성자의 회원 정보와 요청된 평점 및 내용을 포함하여 저장됩니다.
	 */
	@Transactional
	public ReviewCreateResponseDto createReview(Long storeId, String email, ReviewCreateRequestDto requestDto) {

		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.STORE_NOT_FOUND));

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));

		Review review = new Review(store, member, requestDto.getRating(), requestDto.getContent());
		reviewRepository.save(review);

		return new ReviewCreateResponseDto(review);
	}

	/**
	 * 리뷰를 수정합니다.
	 * 해당 리뷰가 지정된 가게에 속하고, 요청자가 작성자인 경우에만 수정이 가능합니다.
	 */
	@Transactional
	public ReviewUpdateResponseDto updateReview(Long storeId, Long reviewId,
		String email,
		ReviewUpdateRequestDto requestDto) {

		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.REVIEW_NOT_FOUND));

		storeRepository.findById(storeId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.STORE_NOT_FOUND));

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));

		if (!review.getStore().getId().equals(storeId)) {
			throw new CustomRuntimeException(ExceptionCode.STORE_REVIEW_MISMATCH);
		}

		if (!review.getMember().getId().equals(member.getId())) {
			throw new CustomRuntimeException(ExceptionCode.EDIT_REVIEW_WRITER_ONLY);
		}

		review.updateContent(requestDto.getContent());
		Review savedReview = reviewRepository.save(review);
		return new ReviewUpdateResponseDto(savedReview);
	}

	/**
	 * 특정 가게에 작성된 모든 리뷰를 페이징 처리하여 조회합니다.
	 */
	@Transactional(readOnly = true)
	public Slice<ReviewListResponseDto> getAllReviewsByStore(Long storeId, Pageable pageable) {

		storeRepository.findById(storeId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.STORE_NOT_FOUND));

		Slice<ReviewListResponseDto> reviewsPage = reviewRepository.findByStoreId(storeId, pageable)
			.map(ReviewListResponseDto::new);
		return reviewsPage;
	}

	/**
	 * 리뷰를 삭제합니다.
	 * 작성자 본인만 삭제할 수 있으며,
	 * 리뷰가 없거나 가게와 리뷰가 맞지 않거나 작성자가 아니면 예외가 발생합니다.
	 */
	@Transactional
	public void deleteReview(Long storeId, Long reviewId, String email) {

		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.REVIEW_NOT_FOUND));

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));

		if (!review.getStore().getId().equals(storeId)) {
			throw new CustomRuntimeException(ExceptionCode.STORE_REVIEW_MISMATCH);
		}

		if (!review.getMember().getId().equals(member.getId())) {
			throw new CustomRuntimeException(ExceptionCode.DELETE_REVIEW_WRITER_ONLY);
		}

		reviewRepository.delete(review);
	}
}
