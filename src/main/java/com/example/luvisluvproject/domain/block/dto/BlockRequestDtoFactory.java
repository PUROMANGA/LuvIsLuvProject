package com.example.luvisluvproject.domain.block.dto;

import com.example.luvisluvproject.domain.block.entity.Block;

/**
 * BlockRequestDtoFactory
 *
 * 신고 처리 또는 시스템 정책 등에 의해 자동으로 차단 요청을 생성할 때 사용하는 Factory 클래스입니다.
 * 각 정책에 따른 기본값을 적용하여 BlockRequestDto 객체를 간편하게 생성할 수 있습니다.
 */
public class BlockRequestDtoFactory {

	/**
	 * 신고에 의한 자동 차단용 BlockRequestDto 생성 메서드
	 *
	 * @param blockedId 차단 대상 사용자 ID
	 * @return 신고 후 차단을 위한 설정이 적용된 BlockRequestDto
	 *         - 프로필 차단: true
	 *         - 매칭 제외: true
	 *         - 차단 유형: AFTER_REPORT
	 */
	public static BlockRequestDto afterReportBlock(Long blockedId) {
		return new BlockRequestDto(
			blockedId,
			true,   // blockUserAccess
			true,   // excludeFromMatching
			Block.BlockType.AFTER_REPORT
		);
	}
}
 // 팩토리 메서드를 이렇게 쓰는게 아니다?