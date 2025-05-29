package com.example.luvisluvproject.domain.store.service;

import com.example.luvisluvproject.domain.store.dto.request.StoreSaveRequest;
import com.example.luvisluvproject.domain.store.dto.response.StoreResponse;
import com.example.luvisluvproject.domain.store.entity.Store;
import com.example.luvisluvproject.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 가게 등록/수정/삭제 등 비즈니스 로직 처리 서비스
 * 위치기반 서비스는 추가 될 예정 !
 */
@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;

	/**
	 * 사용자의 등록 요청 정보를 바탕으로 Store 엔티티를 생성하고 저장
	 * 위도/경도는 현재 단계에서는 더미 값으로 설정되며,
	 * 이후 OpenAPI 연동을 통해 주소 기반 좌표로 대체됨
	 *
	 * @param request 가게 등록 요청 DTO
	 * @return StoreResponse 응답 DTO
	 */
	public StoreResponse saveStore(StoreSaveRequest request) {
		Store store = new Store(
			request.getName(),
			request.getBusinessNumber(),
			request.getContactNumber(),
			request.getAddress(),
			0.0,  // TODO : OpenAPI 연동 후 실제 위도 값으로 대체
			0.0,  // TODO : OpenAPI 연동 후 실제 경도 값으로 대체
			request.getStatus(),
			request.getStoreType()
		);

		Store saved = storeRepository.save(store);

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
}
