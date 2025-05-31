package com.example.luvisluvproject.domain.store.dto.response;

import com.example.luvisluvproject.domain.store.enums.StoreStatus;
import com.example.luvisluvproject.domain.store.enums.StoreType;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 가게 응답 DTO
 * 현재는 위도/경도 변환 기능이 구현되지 않았으므로
 * 해당 필드는 null로 반환되며  추후 OpenAPI 연동 후 값이 포함될 예정
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
	private Double longitude;  // OpenAPI로 변환된 경도

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
