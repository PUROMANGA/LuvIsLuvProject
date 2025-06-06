package com.example.luvisluvproject.domain.tag.entity;

import com.example.luvisluvproject.domain.tag.enums.TagCategory;
import com.example.luvisluvproject.domain.tag.enums.TagCreatedByType;
import com.example.luvisluvproject.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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
	 * 태그 이름 (예: 캠핑, 고양이, 독서)
	 */
	@Column(nullable = false, unique = true, length = 50)
	private String name;

	/**
	 * 태그 카테고리 (예: 취미, 성격 등)
	 */
	@Column(length = 30)
	private TagCategory category;

	/**
	 * 생성 출처: USER 또는 ADMIN
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TagCreatedByType createdByType;

	/**
	 * 사용 여부 (false면 숨김)
	 */
	@Builder.Default
	private boolean active = true;

	/**
	 * 노출 우선순위 (높을수록 우선 추천)
	 */
	@Builder.Default
	private int priority = 0;

	// name만 수정하는 경우에도 이 메서드를 사용할 수 있음
	public void update(Tag updated) {
		this.name = updated.getName();
		this.category = updated.getCategory();
		this.createdByType = updated.getCreatedByType();
		this.active = updated.isActive();
		this.priority = updated.getPriority();
	}
}
