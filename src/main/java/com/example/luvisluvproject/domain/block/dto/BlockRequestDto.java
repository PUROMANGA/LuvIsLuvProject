package com.example.luvisluvproject.domain.block.dto;

import com.example.luvisluvproject.domain.block.common.BlockType;
import com.example.luvisluvproject.domain.block.entity.Block;
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
	private Long blockedId;

	/**
	 * 차단 유형 (MANUAL, AFTER_REPORT, SYSTEM)
	 * enum을 직접 사용함으로써 타입 안정성과 오타 방지 효과
	 */
	private BlockType blockType;
}
