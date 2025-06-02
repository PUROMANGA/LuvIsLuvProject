package com.example.luvisluvproject.domain.store.service;

import com.example.luvisluvproject.domain.store.dto.request.StoreSaveRequest;
import com.example.luvisluvproject.domain.store.dto.request.StoreUpdateRequest;
import com.example.luvisluvproject.domain.store.dto.response.StoreResponse;
import com.example.luvisluvproject.domain.store.entity.Store;
import com.example.luvisluvproject.domain.store.enums.StoreStatus;
import com.example.luvisluvproject.domain.store.enums.StoreType;
import com.example.luvisluvproject.domain.store.infra.KakaoAddressClient;
import com.example.luvisluvproject.domain.store.infra.dto.KakaoAddressResponse;
import com.example.luvisluvproject.domain.store.repository.StoreRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

/**
 * StoreService 단위 테스트 클래스
 */
class StoreServiceTest {

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private KakaoAddressClient kakaoAddressClient;

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

			KakaoAddressResponse kakaoResponse = mock(KakaoAddressResponse.class);
			given(kakaoResponse.getDocuments()).willReturn(
				Collections.singletonList(mock(KakaoAddressResponse.Document.class)));
			given(kakaoResponse.getLatitude()).willReturn(37.5665);
			given(kakaoResponse.getLongitude()).willReturn(126.9780);
			given(kakaoAddressClient.fetchCoordinates(anyString())).willReturn(kakaoResponse);

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

			KakaoAddressResponse kakaoResponse = mock(KakaoAddressResponse.class);
			given(kakaoResponse.getDocuments()).willReturn(
				Collections.singletonList(mock(KakaoAddressResponse.Document.class)));
			given(kakaoResponse.getLatitude()).willReturn(37.4770);
			given(kakaoResponse.getLongitude()).willReturn(126.9630);
			given(kakaoAddressClient.fetchCoordinates(anyString())).willReturn(kakaoResponse);

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
			verify(storeRepository).save(any(Store.class));
		}

		@Test
		@DisplayName("store_조회_성공_내_주변_가게_리스트")
		void getNearbyStoresSuccess() {
			// given
			double userLat = 37.5665;
			double userLon = 126.9780;
			int radiusMeters = 2000;

			Store store1 = new Store("Cafe Near", 123L, "010-1111-2222", "서울시 종로구", 37.5663, 126.9779, StoreStatus.OPEN,
				StoreType.CAFE); // 아주 가까운 좌표

			given(storeRepository.findAll()).willReturn(List.of(store1));

			// when
			List<StoreResponse> nearbyStores = storeService.getNearbyStores(userLat, userLon, radiusMeters);

			// then
			assertThat(nearbyStores).hasSize(1);
			assertThat(nearbyStores.get(0).getName()).isEqualTo("Cafe Near");
		}

		@Test
		@DisplayName("store_조회_성공_반경_내_가게_없음")
		void getNearbyStoresEmptyResult() {
			// given
			double userLat = 37.0;
			double userLon = 127.0;
			int radiusKm = 1;

			Store store1 = new Store("Cafe Far", 789L, "010-0000-0000", "부산시 중구", 35.1796, 129.0756, StoreStatus.OPEN,
				StoreType.CAFE);

			given(storeRepository.findAll()).willReturn(List.of(store1));

			// when
			List<StoreResponse> nearbyStores = storeService.getNearbyStores(userLat, userLon, radiusKm);

			// then
			assertThat(nearbyStores).isEmpty();
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

			StoreUpdateRequest request = new StoreUpdateRequest("없는 가게", "010-0000-0000", "서울시 강남구", StoreStatus.CLOSED,
				StoreType.CAFE);

			given(storeRepository.findById(invalidId)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> storeService.updateStore(invalidId, request)).isInstanceOf(
				CustomRuntimeException.class).hasMessage(ExceptionCode.STORE_NOT_FOUND.getMessage());

			verify(storeRepository).findById(invalidId);
		}

		@Test
		@DisplayName("store_삭제_실패_존재하지않는_ID")
		void storeDeleteFailWhenIdNotFound() {
			// given
			Long invalidId = 999L;

			given(storeRepository.findById(invalidId)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> storeService.deleteStore(invalidId)).isInstanceOf(CustomRuntimeException.class)
				.hasMessage(ExceptionCode.STORE_NOT_FOUND.getMessage());

			verify(storeRepository).findById(invalidId);
		}

		@Test
		@DisplayName("store_등록_실패_kakao_api_결과없음")
		void storeSaveFailWhenKakaoApiEmptyResult() {
			// given
			StoreSaveRequest request = new StoreSaveRequest("없는 주소", 111222333L, "010-0000-0000", "잘못된 주소",
				StoreStatus.OPEN, StoreType.BAR);

			KakaoAddressResponse kakaoResponse = mock(KakaoAddressResponse.class);
			given(kakaoResponse.getDocuments()).willReturn(Collections.emptyList());
			given(kakaoAddressClient.fetchCoordinates(anyString())).willReturn(kakaoResponse);

			// when & then
			assertThatThrownBy(() -> storeService.saveStore(request)).isInstanceOf(CustomRuntimeException.class)
				.hasMessage(ExceptionCode.KAKAO_API_EMPTY_RESULT.getMessage());
		}

		@Test
		@DisplayName("store_조회_실패_repository_null_리턴")
		void getNearbyStoresFailWhenRepositoryReturnsNull() {
			// given
			double userLat = 37.5665;
			double userLon = 126.9780;
			int radiusKm = 2;

			given(storeRepository.findAll()).willReturn(null);

			// when & then
			assertThatThrownBy(() -> storeService.getNearbyStores(userLat, userLon, radiusKm)).isInstanceOf(
				NullPointerException.class);
		}

		@Test
		@DisplayName("store_조회_실패_repository_예외_발생")
		void getNearbyStoresFailWhenRepositoryThrowsException() {
			// given
			double userLat = 37.5665;
			double userLon = 126.9780;
			int radiusKm = 2;

			given(storeRepository.findAll()).willThrow(new RuntimeException("DB 오류"));

			// when & then
			assertThatThrownBy(() -> storeService.getNearbyStores(userLat, userLon, radiusKm)).isInstanceOf(
				RuntimeException.class).hasMessageContaining("DB 오류");
		}

	}
}
