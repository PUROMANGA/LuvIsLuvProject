package com.example.luvisluvproject.domain.block.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BlockRequestDto {
	// 차단 대상 사용자의 ID
	private Long blockedId;

	// 프로필 접근 차단
	private boolean blockUserAccess;

	// 매칭 시스템 제외
	private boolean excludeFromMatching;

	// 추천 시스템 제외 (같은 관심사와 같은 유처 추천 기능이 생기면 사용)
	private boolean excludeFromRecommendation;

	// 차단 유형 - "MANUAL"(직접 차단), "AFTER_REPORT"(신고 후 차단), "SYSTEM"(시스템 자동 차단)
	private String blockType;
}