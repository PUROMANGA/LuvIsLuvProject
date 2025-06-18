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
	 * 처리 결과 메시지
	 * 예: "사용자를 차단했습니다." 또는 "차단을 해제했습니다."
	 */
	private String message;

	/**
	 * 차단 대상 사용자의 ID
	 */
	private Long blockedId;

	/**
	 * 차단이 발생한 시각
	 */
	private LocalDateTime blockTime;
}
