package com.example.luvisluvproject.domain.block.dto;

import com.example.luvisluvproject.domain.block.entity.Block;
import com.example.luvisluvproject.domain.block.enums.BlockType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * BlockRequestDto
 * 사용자를 차단할 때 클라이언트로부터 입력받는 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BlockRequestDto {

	/**
	 * 차단 대상 사용자 ID
	 */
	private String blockedEmail;

	/**
	 * 프로필 접근 차단 여부
	 */
	private boolean blockUserAccess;

	/**
	 * 매칭 시스템에서 제외 여부
	 */
	private boolean excludeFromMatching;

	/**
	 * 차단 유형 (MANUAL, AFTER_REPORT, SYSTEM)
	 * enum을 직접 사용함으로써 타입 안정성과 오타 방지 효과
	 */
	private BlockType blockType; // 내부 enum이 아니라 외부 enum을 타입으로 사용
}
