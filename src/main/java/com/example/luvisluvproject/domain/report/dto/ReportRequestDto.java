package com.example.luvisluvproject.domain.report.dto;

import com.example.luvisluvproject.domain.report.entity.ReportReason;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ReportRequestDto
 * 사용자가 신고할 때 전달하는 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequestDto {

	/**
	 * 신고 대상의 고유 ID
	 */
	private Long targetId;

	/**
	 * 신고 사유 (Enum 값: ABUSIVE_LANGUAGE, SPAM 등)
	 */
	private ReportReason reason;

	/**
	 * 상세 설명 (선택 사항)
	 */
	private String description;
}
