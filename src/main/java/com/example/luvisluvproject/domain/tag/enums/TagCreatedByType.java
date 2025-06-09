package com.example.luvisluvproject.domain.tag.enums;

import lombok.Getter;

/**
 * 태그가 생성된 출처를 나타냄.
 * 사용자(User) 또는 관리자(Admin)
 */
@Getter
public enum TagCreatedByType {
	USER("사용자"),
	ADMIN("관리자");

	private final String label;

	TagCreatedByType(String label) {
		this.label = label;
	}
}
