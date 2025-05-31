package com.example.luvisluvproject.domain.store.service;

import com.example.luvisluvproject.domain.store.dto.request.StoreSaveRequest;
import com.example.luvisluvproject.domain.store.dto.request.StoreUpdateRequest;
import com.example.luvisluvproject.domain.store.dto.response.StoreResponse;
import com.example.luvisluvproject.domain.store.entity.Store;
import com.example.luvisluvproject.domain.store.enums.StoreStatus;
import com.example.luvisluvproject.domain.store.enums.StoreType;
import com.example.luvisluvproject.domain.store.repository.StoreRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * StoreService 단위 테스트 클래스
 */
class StoreServiceTest {

	@Mock
	private StoreRepository storeRepository;

	@InjectMocks
	private StoreService storeService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Nested
	@DisplayName("성공 케이스")
	class SuccessCases {

		@Test
		@DisplayName("store_등록_성공")
		void storeCreateSuccess() {
			// given
			StoreSaveRequest request = new StoreSaveRequest("Bella Pub", 1234567890L, "010-1234-5678", "서울시 구로구",
				StoreStatus.OPEN, StoreType.BAR);

			Store mockStore = mock(Store.class);
			given(mockStore.getName()).willReturn("Bella Pub");
			given(mockStore.getStatus()).willReturn(StoreStatus.OPEN);

			given(storeRepository.save(any(Store.class))).willReturn(mockStore);

			// when
			StoreResponse response = storeService.saveStore(request);

			// then
			assertThat(response.getName()).isEqualTo("Bella Pub");
			assertThat(response.getStatus()).isEqualTo(StoreStatus.OPEN);
			verify(storeRepository).save(any(Store.class));
		}

		@Test
		@DisplayName("store_수정_성공")
		void storeUpdateSuccess() {
			// given
			Long storeId = 1L;

			StoreUpdateRequest request = new StoreUpdateRequest("Bella Coffee", "010-9999-8888", "서울시 관악구",
				StoreStatus.CLOSED, StoreType.CAFE);

			Store mockStore = mock(Store.class);
			given(mockStore.getId()).willReturn(storeId);

			given(storeRepository.findById(storeId)).willReturn(Optional.of(mockStore));
			given(storeRepository.save(any(Store.class))).willReturn(mockStore);
			given(mockStore.getName()).willReturn("Bella Coffee");
			given(mockStore.getContactNumber()).willReturn("010-9999-8888");
			given(mockStore.getAddress()).willReturn("서울시 관악구");
			given(mockStore.getStatus()).willReturn(StoreStatus.CLOSED);
			given(mockStore.getStoreType()).willReturn(StoreType.CAFE);

			// when
			StoreResponse response = storeService.updateStore(storeId, request);

			// then
			assertThat(response.getName()).isEqualTo("Bella Coffee");
			assertThat(response.getContactNumber()).isEqualTo("010-9999-8888");
			assertThat(response.getAddress()).isEqualTo("서울시 관악구");
			assertThat(response.getStatus()).isEqualTo(StoreStatus.CLOSED);
			assertThat(response.getStoreType()).isEqualTo(StoreType.CAFE);

			verify(storeRepository).findById(storeId);
			verify(storeRepository).save(any(Store.class));
		}

		@Test
		@DisplayName("store_삭제_성공")
		void storeDeleteSuccess() {
			// given
			Long storeId = 1L;

			Store mockStore = mock(Store.class);
			given(mockStore.getId()).willReturn(storeId);
			given(storeRepository.findById(storeId)).willReturn(Optional.of(mockStore));

			// when
			storeService.deleteStore(storeId);

			// then
			verify(storeRepository).findById(storeId);
			verify(storeRepository).delete(mockStore);
		}
	}

	@Nested
	@DisplayName("실패 케이스")
	class FailureCases {

		@Test
		@DisplayName("store_수정_실패_존재하지않는_ID")
		void storeUpdateFailWhenIdNotFound() {
			// given
			Long invalidId = 999L;

			StoreUpdateRequest request = new StoreUpdateRequest("없는 가게", "010-0000-0000", "서울시 강남구",
				StoreStatus.CLOSED, StoreType.CAFE);

			given(storeRepository.findById(invalidId)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> storeService.updateStore(invalidId, request))
				.isInstanceOf(CustomRuntimeException.class)
				.hasMessage(ExceptionCode.STORE_NOT_FOUND.getMessage());

			verify(storeRepository).findById(invalidId);
		}

		@Test
		@DisplayName("store_삭제_실패_존재하지않는_ID")
		void storeDeleteFailWhenIdNotFound() {
			// given
			Long invalidId = 999L;

			given(storeRepository.findById(invalidId)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> storeService.deleteStore(invalidId))
				.isInstanceOf(CustomRuntimeException.class)
				.hasMessage(ExceptionCode.STORE_NOT_FOUND.getMessage());

			verify(storeRepository).findById(invalidId);
		}
	}
}
