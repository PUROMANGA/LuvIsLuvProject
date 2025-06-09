package com.example.luvisluvproject.domain.review.service;

import static com.example.luvisluvproject.domain.member.enums.UserRole.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.review.dto.ReviewCreateRequestDto;
import com.example.luvisluvproject.domain.review.dto.ReviewCreateResponseDto;
import com.example.luvisluvproject.domain.review.dto.ReviewListResponseDto;
import com.example.luvisluvproject.domain.review.dto.ReviewUpdateRequestDto;
import com.example.luvisluvproject.domain.review.dto.ReviewUpdateResponseDto;
import com.example.luvisluvproject.domain.review.entity.Review;
import com.example.luvisluvproject.domain.review.repository.ReviewRepository;
import com.example.luvisluvproject.domain.store.entity.Store;
import com.example.luvisluvproject.domain.store.enums.StoreStatus;
import com.example.luvisluvproject.domain.store.enums.StoreType;
import com.example.luvisluvproject.domain.store.repository.StoreRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;

class ReviewServiceTest {

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private StoreRepository storeRepository;

	@InjectMocks
	private ReviewService reviewService;

	private Store store;
	private Member member;
	private Review review;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		store = new Store("Test Store", 1234567890L, "010-0000-0000", "서울시 강남구",
			37.5, 127.0, StoreStatus.OPEN, StoreType.CAFE);

		member = new Member(1L, "test", "test@email.com", "pass123",
			LocalDate.of(1995, 1, 1), USER, false, 0L);

		review = new Review(store, member, 5, "맛있어요!");
	}

	@Nested
	class CreateReviewTest {

		@Test
		@DisplayName("리뷰 생성 성공")
		void createReview_success() {
			Long storeId = 1L;
			ReviewCreateRequestDto dto = new ReviewCreateRequestDto(5, "좋아요");

			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> {
				Review r = inv.getArgument(0);
				return r;
			});

			ReviewCreateResponseDto response = reviewService.createReview(storeId, member, dto);

			assertNotNull(response);
			assertEquals("후기를 작성했어요.", response.getMessage());
		}

		@Test
		@DisplayName("가게가 존재하지 않아 리뷰 생성 실패")
		void createReview_storeNotFound() {
			when(storeRepository.findById(anyLong())).thenReturn(Optional.empty());

			assertThrows(CustomRuntimeException.class,
				() -> reviewService.createReview(1L, member, new ReviewCreateRequestDto(5, "내용")));
		}
	}

	@Nested
	class UpdateReviewTest {

		private Store store;
		private Member member;

		@BeforeEach
		void setUp() {
			store = new Store("가게", 1234L, "12332131", "서울", 37.5, 127.0, StoreStatus.OPEN, StoreType.CAFE);
			member = new Member("user", "user@email.com", "password", LocalDate.parse("2000-01-01"), USER);

			// ID 설정
			ReflectionTestUtils.setField(store, "id", 1L);
			ReflectionTestUtils.setField(member, "id", 1L);
		}

		@Test
		@DisplayName("리뷰 수정 - 성공")
		void shouldUpdateReviewSuccessfully() {
			// given
			ReviewUpdateRequestDto dto = new ReviewUpdateRequestDto("수정된 내용");
			Review savedReview = new Review(store, member, 5, "기존 내용");
			ReflectionTestUtils.setField(savedReview, "id", 1L);

			when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(savedReview));

			// when
			ReviewUpdateResponseDto response = reviewService.updateReview(1L, 1L, member.getId(), dto);

			// then
			assertNotNull(response);
			assertEquals("후기를 새롭게 수정했습니다.", response.getMessage());
			assertEquals("수정된 내용", savedReview.getContent());
		}

		@Test
		@DisplayName("리뷰 수정 - 작성자가 아니면 실패")
		void shouldThrowExceptionWhenMemberIsNotAuthor() {
			// given
			ReviewUpdateRequestDto dto = new ReviewUpdateRequestDto("수정 시도");
			Review savedReview = new Review(store, member, 5, "기존 내용");
			ReflectionTestUtils.setField(savedReview, "id", 1L);

			when(reviewRepository.findById(1L)).thenReturn(Optional.of(savedReview));

			// when & then
			assertThrows(CustomRuntimeException.class,
				() -> reviewService.updateReview(1L, 1L, 999L, dto));
		}
	}

	@Nested
	class GetReviewDetailTest {

		private Store store;
		private Member member;
		private Review review;

		@BeforeEach
		void setUp() {
			store = new Store("가게", 1234L, "12332131", "서울", 37.5, 127.0, StoreStatus.OPEN, StoreType.CAFE);
			member = new Member("user", "user@email.com", "password", LocalDate.parse("2000-01-01"), USER);
			review = new Review(store, member, 5, "좋았어요");

			// ID 수동 설정
			ReflectionTestUtils.setField(member, "id", 1L);
			ReflectionTestUtils.setField(store, "id", 1L);
			ReflectionTestUtils.setField(review, "id", 1L);
		}

		@Test
		@DisplayName("리뷰 상세 조회 - 성공")
		void shouldGetReviewDetailSuccessfully() {
			// given
			when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

			// when
			var result = reviewService.getReviewDetail(1L, 1L);

			// then
			assertNotNull(result);
			assertEquals(review.getContent(), result.getContent());
		}

		@Test
		@DisplayName("리뷰 상세 조회 - 가게 매칭 실패 시 예외 발생")
		void shouldThrowExceptionWhenStoreDoesNotMatch() {
			// given
			Store otherStore = new Store("다른 가게", 9999L, "", "", 0.0, 0.0, StoreStatus.OPEN, StoreType.BAR);
			Review wrongStoreReview = new Review(otherStore, member, 4, "별로였어요");

			// ID 설정
			ReflectionTestUtils.setField(otherStore, "id", 2L);
			ReflectionTestUtils.setField(wrongStoreReview, "id", 1L);
			ReflectionTestUtils.setField(member, "id", 1L);

			when(reviewRepository.findById(1L)).thenReturn(Optional.of(wrongStoreReview));

			// when & then
			assertThrows(CustomRuntimeException.class,
				() -> reviewService.getReviewDetail(1L, 1L));
		}
	}

	@Nested
	class GetAllReviewsByStoreTest {

		@Test
		@DisplayName("가게 리뷰 목록 조회 성공")
		void getAllReviewsByStore_success() {
			when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
			when(reviewRepository.findByStoreId(store)).thenReturn(List.of(
				new Review(store, member, 4, "리뷰1"),
				new Review(store, member, 5, "리뷰2")
			));

			List<ReviewListResponseDto> reviews = reviewService.getAllReviewsByStore(1L);

			assertEquals(2, reviews.size());
		}
	}

	@Nested
	class DeleteReviewTest {

		private Store store;
		private Member member;
		private Review review;

		@BeforeEach
		void setUp() {
			store = new Store("가게", 1234L, "12332131", "서울", 37.5, 127.0, StoreStatus.OPEN, StoreType.CAFE);
			member = new Member("user", "user@email.com", "password", LocalDate.parse("2000-01-01"), USER);
			review = new Review(store, member, 5, "리뷰 내용");

			// ID 강제 설정 (setter 없이)
			ReflectionTestUtils.setField(member, "id", 1L);
			ReflectionTestUtils.setField(store, "id", 1L);
			ReflectionTestUtils.setField(review, "id", 1L);
		}

		@Test
		@DisplayName("리뷰 삭제 성공")
		void deleteReview_success() {
			when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

			assertDoesNotThrow(() -> reviewService.deleteReview(1L, 1L, member.getId()));
			verify(reviewRepository, times(1)).delete(review);
		}

		@Test
		@DisplayName("작성자가 아니면 삭제 실패")
		void deleteReview_notWriter() {
			when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

			assertThrows(CustomRuntimeException.class,
				() -> reviewService.deleteReview(1L, 1L, 999L)); // 다른 ID
			verify(reviewRepository, never()).delete(any());
		}
	}

}
