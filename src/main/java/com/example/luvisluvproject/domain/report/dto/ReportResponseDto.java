package com.example.luvisluvproject.domain.report.dto;

import com.example.luvisluvproject.domain.report.entity.Report;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ReportResponseDto
 * 신고 처리 완료 후 클라이언트에게 반환하는 응답 DTO
 */
@Getter
@AllArgsConstructor
public class ReportResponseDto {
	private Long reportedId;
	private String reportedName;

	public ReportResponseDto(Report report) {
		this.reportedId = report.getReported().getId();
		this.reportedName = report.getReported().getName();
	}
}
