package com.example.luvisluvproject.domain.tag.enums;

public enum TagCategory {
	HOBBY, GENDER_IDENTITY, SEXUAL_ORIENTATION;

	public static TagCategory from(String value) {
		return TagCategory.valueOf(value.toUpperCase());
	}
}