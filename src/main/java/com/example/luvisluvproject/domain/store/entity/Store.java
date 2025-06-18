package com.example.luvisluvproject.domain.store.entity;

import com.example.luvisluvproject.domain.store.enums.StoreStatus;
import com.example.luvisluvproject.domain.store.enums.StoreType;
import com.example.luvisluvproject.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Store 엔티티는 가게 정보를 나타냄
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stores")
public class Store extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false, unique = true)
	private Long businessNumber;

	@Column(length = 20)
	private String contactNumber;

	@Column(length = 255)
	private String address;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Double longitude;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StoreStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StoreType storeType;

	/**
	 * Store 엔티티 생성자
	 */
	@Builder
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

	public void update(String name, String contactNumber, String address, Double latitude, Double longitude,
		StoreStatus status, StoreType storeType) {
		this.name = name;
		this.contactNumber = contactNumber;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.status = status;
		this.storeType = storeType;
	}
}
