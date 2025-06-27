package com.example.luvisluvproject.domain.block.dto;

import java.time.LocalDateTime;

import com.example.luvisluvproject.domain.block.entity.Block;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 사용자 차단 또는 차단 해제 시 클라이언트에게 반환되는 응답 DTO
 */
@Getter
@AllArgsConstructor
public class BlockResponseDto {

	/**
	 * 차단한 사용자의 ID
	 */
	private Long blockerId;

	/**
	 * 차단 대상 사용자의 ID
	 */
	private Long blockedId;

	/**
	 * 차단이 발생한 시각
	 */
	private LocalDateTime blockTime;

	public BlockResponseDto(Block block) {
		this.blockerId = block.getBlocker().getId();
		this.blockedId = block.getBlocked().getId();
		this.blockTime = block.getCreatTime();
	}
}
