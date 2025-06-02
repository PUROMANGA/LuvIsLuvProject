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

	// DM(쪽지) 차단
	private boolean blockDM;

	// 프로필 접근 차단
	private boolean blockProfileAccess;

	// 게시물 및 댓글 등 콘텐츠 차단
	private boolean blockContent;

	// 매칭 시스템 제외
	private boolean excludeFromMatching;

	// 추천 시스템 제외
	private boolean excludeFromRecommendation;

	// 차단 유형 - "MANUAL"(직접 차단), "AFTER_REPORT"(신고 후 차단), "SYSTEM"(시스템 자동 차단)
	private String blockType;
}