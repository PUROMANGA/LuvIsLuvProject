package com.example.luvisluvproject.domain.store.dto.request;

import com.example.luvisluvproject.domain.store.enums.StoreStatus;
import com.example.luvisluvproject.domain.store.enums.StoreType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 가게 등록 요청 DTO
 * 위도/경도는 추후 OpenAPI로 변환되므로 이 요청에서는 받지 않음
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
}