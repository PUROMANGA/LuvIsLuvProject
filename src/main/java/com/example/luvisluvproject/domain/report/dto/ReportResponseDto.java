package com.example.luvisluvproject.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * ReportResponseDto
 * 신고 처리 완료 후 클라이언트에게 반환하는 응답 DTO
 */
@Getter
@AllArgsConstructor
public class ReportResponseDto {

	/**
	 * 처리 결과 메시지 (예: "신고가 정상적으로 접수되었습니다.")
	 */
	private String message;

	/**
	 * 신고가 접수된 시각
	 */
	private LocalDateTime createdAt;
}
