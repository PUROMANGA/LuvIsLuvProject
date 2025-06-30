package com.example.luvisluvproject.domain.review;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.review.dto.ReviewCreateRequestDto;
import com.example.luvisluvproject.domain.review.dto.ReviewCreateResponseDto;
import com.example.luvisluvproject.domain.review.dto.ReviewUpdateRequestDto;
import com.example.luvisluvproject.domain.review.dto.ReviewUpdateResponseDto;
import com.example.luvisluvproject.domain.review.entity.Review;
import com.example.luvisluvproject.domain.review.repository.ReviewRepository;
import com.example.luvisluvproject.domain.review.service.ReviewService;
import com.example.luvisluvproject.domain.store.entity.Store;
import com.example.luvisluvproject.domain.store.repository.StoreRepository;
import com.example.luvisluvproject.global.common.TestFactory;

@ExtendWith(MockitoExtension.class)
public class ReviewTest {

	@Spy
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Spy
	private TestFactory testFactory = new TestFactory(passwordEncoder);

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private ReviewRepository reviewRepository;

	@InjectMocks
	private ReviewService reviewService;

	@Test
	@DisplayName("리뷰 작성 테스트")
	void createReviewTest() {

		//setUp
		Long storeId = 1L;
		Long memberId = 1L;
		String email = "park1@email.com";
		Store mockStore = mock(Store.class);
		Member mockMember = mock(Member.class);

		ReviewCreateRequestDto reviewCreateRequestDto = new ReviewCreateRequestDto(5, "리뷰내용");

		//given
		given(mockStore.getId()).willReturn(storeId);
		given(mockMember.getId()).willReturn(memberId);
		given(storeRepository.findById(anyLong())).willReturn(Optional.of(mockStore));
		given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(mockMember));

		//when
		ReviewCreateResponseDto createResponseDto = reviewService.createReview(storeId, email, reviewCreateRequestDto);

		//then
		assertThat(createResponseDto.getContent()).isEqualTo("리뷰내용");
	}

	@Test
	@DisplayName("리뷰 수정 테스트")
	void updateReviewTest() {

		//setUp
		Long storeId = 1L;
		Long memberId = 1L;
		String email = "park1@email.com";
		Store mockStore = mock(Store.class);
		Member mockMember = mock(Member.class);

		ReviewUpdateRequestDto requestDto = new ReviewUpdateRequestDto("수정된 리뷰내용");
		Review review = testFactory.reviewTestOf(mockStore, mockMember, 3, "내용");
		ReflectionTestUtils.setField(review, "id", 1L);

		//given
		given(mockStore.getId()).willReturn(storeId);
		given(mockMember.getId()).willReturn(memberId);
		given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));
		given(storeRepository.findById(anyLong())).willReturn(Optional.of(mockStore));
		given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(mockMember));
		given(reviewRepository.save(review)).willReturn(review);

		//when
		ReviewUpdateResponseDto reviewUpdateResponseDto = reviewService.updateReview(storeId, review.getId(), email, requestDto);

		//then
		assertThat(reviewUpdateResponseDto.getContent()).isEqualTo("수정된 리뷰내용");
	}

	@Test
	@DisplayName("리뷰 조회 테스트")
	void getAllReviewsByStoreTest() {

		//setUp
		Long storeId = 1L;
		Store mockStore = mock(Store.class);
		Member mockMember = mock(Member.class);

		Review review = testFactory.reviewTestOf(mockStore, mockMember, 3, "내용");
		ReflectionTestUtils.setField(review, "id", 1L);

		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "creatTime"));
		List<Review> reviewList = new ArrayList<>();
		Slice<Review> reviews = new SliceImpl<>(reviewList, pageable, false);

		//given
		given(storeRepository.findById(anyLong())).willReturn(Optional.of(mockStore));
		given(reviewRepository.findByStoreId(anyLong(), eq(pageable))).willReturn(reviews);

		//when
		reviewService.getAllReviewsByStore(storeId, pageable);
	}

	@Test
	@DisplayName("리뷰 조회 테스트")
	void deleteReviewTest() {

		//setUp
		Long storeId = 1L;
		Long memberId = 1L;
		String email = "park1@email.com";
		Store mockStore = mock(Store.class);
		Member mockMember = mock(Member.class);

		Review review = testFactory.reviewTestOf(mockStore, mockMember, 3, "내용");
		ReflectionTestUtils.setField(review, "id", 1L);

		//given
		given(mockStore.getId()).willReturn(storeId);
		given(mockMember.getId()).willReturn(memberId);
		given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));
		given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(mockMember));

		//when
		reviewService.deleteReview(storeId, review.getId(), email);
	}



}
