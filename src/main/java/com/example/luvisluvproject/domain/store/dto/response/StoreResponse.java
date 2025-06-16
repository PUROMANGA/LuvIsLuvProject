package com.example.luvisluvproject.domain.store.dto.response;

import com.example.luvisluvproject.domain.store.entity.Store;
import com.example.luvisluvproject.domain.store.enums.StoreStatus;
import com.example.luvisluvproject.domain.store.enums.StoreType;

import lombok.Builder;
import lombok.Getter;

/**
 * 가게 응답 DTO
 *
 */
@Getter
@Builder
public class StoreResponse {

	private Long id;
	private String name;
	private Long businessNumber;
	private String contactNumber;
	private String address;
	private StoreStatus status;
	private StoreType storeType;
	private Double latitude;   // OpenAPI로 변환된 위도
	private Double longitude; // OpenAPI로 변환된 경도

	public static StoreResponse from(Store store) {
		return StoreResponse.builder()
			.id(store.getId())
			.name(store.getName())
			.businessNumber(store.getBusinessNumber())
			.contactNumber(store.getContactNumber())
			.address(store.getAddress())
			.status(store.getStatus())
			.storeType(store.getStoreType())
			.latitude(store.getLatitude())
			.longitude(store.getLongitude())
			.build();
	}

	/**
	 * 테스트용 생성자
	 */
	public StoreResponse(Long id, String name, Long businessNumber, String contactNumber, String address,
		StoreStatus status, StoreType storeType, Double latitude, Double longitude) {
		this.id = id;
		this.name = name;
		this.businessNumber = businessNumber;
		this.contactNumber = contactNumber;
		this.address = address;
		this.status = status;
		this.storeType = storeType;
		this.latitude = latitude;
		this.longitude = longitude;
	}
}
