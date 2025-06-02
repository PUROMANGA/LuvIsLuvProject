package com.example.luvisluvproject.domain.store.dto.request;

import com.example.luvisluvproject.domain.store.enums.StoreStatus;
import com.example.luvisluvproject.domain.store.enums.StoreType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 가게 수정 요청 DTO
 * 주소가 수정된 경우, 백엔드에서 OpenAPI로 위도/경도를 재조회
 */
@Getter
public class StoreUpdateRequest {

	@NotBlank(message = "가게 이름은 필수입니다.")
	private String name;

	private String contactNumber;

	private String address;

	@NotNull(message = "가게 상태는 필수입니다.")
	private StoreStatus status;

	@NotNull(message = "가게 유형은 필수입니다.")
	private StoreType storeType;

	/**
	 * 테스트용 생성자 (편의상 넣음)
	 */
	public StoreUpdateRequest(String name, String contactNumber,
		String address, StoreStatus status, StoreType storeType) {
		this.name = name;
		this.contactNumber = contactNumber;
		this.address = address;
		this.status = status;
		this.storeType = storeType;
	}
}
