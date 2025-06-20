package com.example.luvisluvproject.domain.block.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * UnblockResponseDto
 * 사용자가 차단 해제 요청을 했을 때 반환되는 응답 DTO입니다.
 */
@Getter
@AllArgsConstructor
public class UnblockResponseDto {

	/**
	 * 차단 해제된 사용자 ID
	 */
	private Long unblockedUserId;
}
