package com.example.luvisluvproject.domain.report.dto;

import com.example.luvisluvproject.domain.report.entity.ReportReason;
import lombok.Getter;

@Getter
public class ReportRequestDto {
	private Long reportedId;
	private ReportReason reason;
	private String description;
}