package com.example.luvisluvproject.domain.tag.entity;

import com.example.luvisluvproject.domain.tag.enums.TagCategory;
import com.example.luvisluvproject.domain.tag.enums.TagCreatedByType;
import com.example.luvisluvproject.global.common.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 태그 도메인 엔티티
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "tags", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"name"})
})
public class Tag extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 태그 이름 (예: 독서, 캠핑)
	 */
	@Column(nullable = false, unique = true, length = 50)
	private String name;

	/**
	 * 태그 카테고리 (HOBBY, GENDER_IDENTITY, 등)
	 */
	@Enumerated(EnumType.STRING)
	@Column(length = 30)
	private TagCategory category;

	/**
	 * 생성 주체 (USER 또는 ADMIN)
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TagCreatedByType createdByType;

	/**
	 * 태그 활성 여부 (false일 경우 숨김)
	 */
	@Column(nullable = false)
	private boolean active = true;

	/**
	 * 노출 우선순위 (높을수록 우선 노출)
	 */
	@Column(nullable = false)
	private int priority = 0;

	/**
	 * 태그 정보 수정
	 */
	public void update(String name, TagCategory category, TagCreatedByType createdByType, boolean active, int priority) {
		this.name = name;
		this.category = category;
		this.createdByType = createdByType;
		this.active = active;
		this.priority = priority;
	}

	/**
	 * 생성자 (update와 구분)
	 */
	public Tag(String name, TagCategory category, TagCreatedByType createdByType, boolean active, int priority) {
		this.name = name;
		this.category = category;
		this.createdByType = createdByType;
		this.active = active;
		this.priority = priority;
	}
}
