package com.example.luvisluvproject.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * ReportResponseDto
 * 신고 처리 완료 후 클라이언트에게 반환하는 응답 DTO
 * 메시지 필드 제거
 * createdAt만 반환
 */
@Getter
@AllArgsConstructor
public class ReportResponseDto {

	/**
	 * 신고가 접수된 시각
	 */
	private LocalDateTime createdAt;
}
