package com.example.luvisluvproject.domain.block.dto;

public class BlockRequestDtoFactory {

	public static BlockRequestDto afterReportBlock(Long blockedId) {
		return new BlockRequestDto(
			blockedId,
			true,   // blockUserAccess
			true,   // excludeFromMatching
			true,   // excludeFromRecommendation
			"AFTER_REPORT"
		);
	}
}
