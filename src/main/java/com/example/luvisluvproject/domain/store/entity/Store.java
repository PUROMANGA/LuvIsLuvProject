package com.example.luvisluvproject.domain.store.entity;

import com.example.luvisluvproject.domain.store.enums.StoreStatus;
import com.example.luvisluvproject.domain.store.enums.StoreType;
import com.example.luvisluvproject.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Store 엔티티는 가게 정보를 나타냄
 * 위도(latitude), 경도(longitude)는 내 주변 위치 서비스를 위해 사용됨
 * StoreStatus, StoreType 열거형을 통해 가게 상태 및 업종 유형을 관리함
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stores")
public class Store extends BaseEntity {

	/**
	 * 가게 고유 식별자 (PK)
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 가게 이름
	 */
	@Column(nullable = false, length = 100)
	private String name;

	/**
	 * 사업자 등록 번호
	 */
	@Column(nullable = false, unique = true)
	private Long businessNumber;

	/**
	 * 가게 전화번호
	 */
	@Column(length = 20)
	private String contactNumber;

	/**
	 * 가게 주소
	 */
	@Column(length = 255)
	private String address;

	/**
	 * 위도
	 */
	@Column(nullable = false)
	private Double latitude;

	/**
	 * 경도
	 */
	@Column(nullable = false)
	private Double longitude;

	/**
	 * 가게 상태 (영업 중, 폐업 등)
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StoreStatus status;

	/**
	 * 가게 유형 (예: BAR, CAFE 등)
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StoreType storeType;

	/**
	 * Store 엔티티 생성자
	 *
	 * @param name           가게 이름
	 * @param businessNumber 사업자등록번호
	 * @param contactNumber  연락처
	 * @param address        주소
	 * @param latitude       위도
	 * @param longitude      경도
	 * @param status         가게 상태
	 * @param storeType      가게 유형
	 */
	public Store(String name, Long businessNumber, String contactNumber, String address,
		Double latitude, Double longitude, StoreStatus status, StoreType storeType) {
		this.name = name;
		this.businessNumber = businessNumber;
		this.contactNumber = contactNumber;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.status = status;
		this.storeType = storeType;
	}

	/**
	 * 가게 정보 수정 메서드
	 *
	 * @param name         새로운 이름
	 * @param contactNumber 새로운 연락처
	 * @param address      새로운 주소
	 * @param status       새로운 상태
	 * @param storeType    새로운 업종
	 */
	public void update(String name, String contactNumber, String address,
		StoreStatus status, StoreType storeType) {
		this.name = name;
		this.contactNumber = contactNumber;
		this.address = address;
		this.status = status;
		this.storeType = storeType;
	}
}
