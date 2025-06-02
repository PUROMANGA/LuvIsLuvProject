package com.example.luvisluvproject.domain.store.dto.request;

import com.example.luvisluvproject.domain.store.enums.StoreStatus;
import com.example.luvisluvproject.domain.store.enums.StoreType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 가게 등록 요청 DTO
 * 테스트 및 객체 생성을 위한 생성자 추가
 */
@Getter
public class StoreSaveRequest {

	@NotBlank(message = "가게 이름은 필수입니다.")
	private String name;

	@NotNull(message = "사업자 등록 번호는 필수입니다.")
	private Long businessNumber;

	private String contactNumber;

	private String address;

	@NotNull(message = "가게 상태는 필수입니다.")
	private StoreStatus status;

	@NotNull(message = "가게 유형은 필수입니다.")
	private StoreType storeType;

	/**
	 * 테스트용 생성자 (편의상 넣음)
	 */
	public StoreSaveRequest(String name, Long businessNumber, String contactNumber,
		String address, StoreStatus status, StoreType storeType) {
		this.name = name;
		this.businessNumber = businessNumber;
		this.contactNumber = contactNumber;
		this.address = address;
		this.status = status;
		this.storeType = storeType;
	}
}
