package com.example.luvisluvproject.domain.store.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.luvisluvproject.domain.store.dto.request.StoreSaveRequest;
import com.example.luvisluvproject.domain.store.dto.request.StoreUpdateRequest;
import com.example.luvisluvproject.domain.store.dto.response.StoreResponse;
import com.example.luvisluvproject.domain.store.entity.Store;
import com.example.luvisluvproject.domain.store.infra.KakaoAddressClient;
import com.example.luvisluvproject.domain.store.infra.dto.KakaoAddressResponse;
import com.example.luvisluvproject.domain.store.repository.StoreRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

/**
 * 가게 등록/수정/삭제 등 비즈니스 로직 처리 서비스
 *
 */
@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final KakaoAddressClient kakaoAddressClient;

	/**
	 * 사용자의 등록 요청 정보를 바탕으로 Store 엔티티를 생성하고 저장
	 * Kakao API를 통해 주소 → 위도/경도 좌표 변환 수행
	 *
	 * @param request 가게 등록 요청 DTO
	 * @return StoreResponse 응답 DTO
	 */
	public StoreResponse saveStore(StoreSaveRequest request) {
		// 1. Kakao API 호출
		KakaoAddressResponse kakaoResponse = kakaoAddressClient.fetchCoordinates(request.getAddress());

		// 2. 위도/경도 Optional 추출 및 예외 처리
		Double latitude = kakaoResponse.getLatitude()
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.KAKAO_API_EMPTY_RESULT));
		Double longitude = kakaoResponse.getLongitude()
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.KAKAO_API_EMPTY_RESULT));

		// 3. Store 생성 및 저장
		Store store = Store.builder()
			.name(request.getName())
			.businessNumber(request.getBusinessNumber())
			.contactNumber(request.getContactNumber())
			.address(request.getAddress())
			.latitude(latitude)
			.longitude(longitude)
			.status(request.getStatus())
			.storeType(request.getStoreType())
			.build();

		Store saved = storeRepository.save(store);

		// 사업자 번호 검증 api ?
		return StoreResponse.builder()
			.id(saved.getId())
			.name(saved.getName())
			.businessNumber(saved.getBusinessNumber())
			.contactNumber(saved.getContactNumber())
			.address(saved.getAddress())
			.status(saved.getStatus())
			.storeType(saved.getStoreType())
			.latitude(saved.getLatitude())
			.longitude(saved.getLongitude())
			.build();
	}

	/**
	 * 가게 정보 수정
	 * 주소가 변경되었을 경우, Kakao API를 통해 위도/경도 좌표 갱신
	 *
	 * @param storeId 수정할 가게의 ID
	 * @param request 수정 요청 DTO
	 * @return 수정된 가게 정보 응답 DTO
	 */
	public StoreResponse updateStore(Long storeId, StoreUpdateRequest request) {
		// 1. 기존 가게 조회
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.STORE_NOT_FOUND));

		// 2. Kakao API 호출
		// 카카오 종속
		KakaoAddressResponse kakaoResponse = kakaoAddressClient.fetchCoordinates(request.getAddress());

		// 같음
		// 위도 경도 한묶음
		// dto 따로?
		Double latitude = kakaoResponse.getLatitude()
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.KAKAO_API_EMPTY_RESULT));
		Double longitude = kakaoResponse.getLongitude()
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.KAKAO_API_EMPTY_RESULT));

		// 3. 기존 Store 정보 업데이트
		store.update(
			request.getName(),
			request.getContactNumber(),
			request.getAddress(),
			latitude,
			longitude,
			request.getStatus(),
			request.getStoreType()
		);

		Store updated = storeRepository.save(store);

		return StoreResponse.builder()
			.id(updated.getId())
			.name(updated.getName())
			.businessNumber(updated.getBusinessNumber())
			.contactNumber(updated.getContactNumber())
			.address(updated.getAddress())
			.status(updated.getStatus())
			.storeType(updated.getStoreType())
			.latitude(updated.getLatitude())
			.longitude(updated.getLongitude())
			.build();
	}

	/**
	 * 가게 삭제 - Hard delete 방식
	 *
	 * @param storeId 삭제할 가게 ID
	 */
	public void deleteStore(Long storeId) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.STORE_NOT_FOUND));

		storeRepository.delete(store);
	}

	/**
	 * 주어진 좌표(lat, lng)를 기준으로 일정 거리 반경(radiusMeters) 내 가게 목록 조회
	 *
	 * @param lat 위도
	 * @param lng 경도
	 * @param radiusMeters 반경 (단위: 미터)
	 * @return 반경 내의 가게 응답 리스트
	 */
	public List<StoreResponse> getNearbyStores(double lat, double lng, int radiusMeters) {
		List<Store> allStores = storeRepository.findAll();

		return allStores.stream().filter(store -> {
			double distance = calculateDistance(lat, lng, store.getLatitude(), store.getLongitude());
			return distance <= radiusMeters;
		}).map(StoreResponse::from).toList();
	}

	/**
	 * 두 좌표 간의 거리 계산 (단위: 미터) // 엔티티 안에
	 *
	 * @param lat1 위도1
	 * @param lng1 경도1
	 * @param lat2 위도2
	 * @param lng2 경도2
	 * @return 거리 (단위: 미터)
	 */
	private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
		final int EARTH_RADIUS = 6371000; // 지구 반지름(m)

		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lng2 - lng1);

		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(
			Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return EARTH_RADIUS * c;
	}

}
