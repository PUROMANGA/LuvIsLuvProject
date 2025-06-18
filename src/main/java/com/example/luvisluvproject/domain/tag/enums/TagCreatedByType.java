package com.example.luvisluvproject.domain.tag.enums;

import lombok.Getter;

/**
 * 태그 생성 주체 (사용자 or 관리자)
 */
@Getter
public enum TagCreatedByType {

	USER("사용자"),
	ADMIN("관리자");

	/**
	 * 프론트 표현용 라벨
	 */
	private final String label;

	TagCreatedByType(String label) {
		this.label = label;
	}

	/**
	 * 문자열로부터 enum 매핑
	 */
	public static TagCreatedByType from(String value) {
		for (TagCreatedByType type : TagCreatedByType.values()) {
			if (type.name().equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Invalid TagCreatedByType: " + value);
	}
}
