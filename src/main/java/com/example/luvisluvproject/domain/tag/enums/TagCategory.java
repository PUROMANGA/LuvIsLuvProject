package com.example.luvisluvproject.domain.tag.enums;

public enum TagCategory {
	HOBBY,
	GENDER_IDENTITY,
	SEXUAL_ORIENTATION;

	/**
	 * 문자열을 대소문자 구분 없이 Enum으로 변환
	 * (예: "hobby" → HOBBY)
	 */
	public static TagCategory from(String value) {
		return TagCategory.valueOf(value.toUpperCase());
	}
}
