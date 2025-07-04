package com.example.luvisluvproject.domain.recommendedmembers.enums;

import lombok.Getter;

@Getter
public enum RecommendedCategory {
	TAG("태그"), COLLAB("협력");

	private final String reason;

	RecommendedCategory(String reason) {
		this.reason = reason;
	}
}
