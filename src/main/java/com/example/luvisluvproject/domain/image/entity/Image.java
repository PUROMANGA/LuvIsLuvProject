package com.example.luvisluvproject.domain.image.entity;

import com.example.luvisluvproject.domain.store.entity.Store;
import com.example.luvisluvproject.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Table(name = "images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Image extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * S3에 저장된 이미지 경로 (예: store/uuid.jpg)
	 */
	@Column(nullable = false)
	private String imagePath;

	/**
	 * 원본 파일 이름 (예: bella.png)
	 */
	private String originalName;

	/**
	 * 이미지가 연관된 가게 (다대일)
	 * 프로필 이미지 향후 추가 예정
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	private Store store;

	@Builder
	public Image(String imagePath, String originalName, Store store) {
		this.imagePath = imagePath;
		this.originalName = originalName;
		this.store = store;
	}

	public void updateStore(Store store) {
		this.store = store;
	}
}
