package com.example.luvisluvproject.admin.sanction.dto;

import com.example.luvisluvproject.domain.report.entity.ReportReason;

import lombok.Getter;

/**
 * 개인당 신고
 */
@Getter
public class SanctionReportDto {

	private Long reportId;

	private Long reporterId;

	private String reporterEmail;

	private String reporterName;

	private Long reportedId;

	private String reportedEmail;

	private String reportedName;

	private ReportReason reportReason;

	private String description;

}
