package com.example.luvisluvproject.domain.block.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 사용자 차단 또는 차단 해제 시 클라이언트에게 반환되는 응답 DTO
 */
@Getter
@AllArgsConstructor
public class BlockResponseDto {

	/**
	 * 차단 대상 사용자의 ID
	 */
	private Long blockedId;
}
