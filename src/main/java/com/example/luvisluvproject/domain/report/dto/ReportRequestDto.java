package com.example.luvisluvproject.domain.report.dto;

import com.example.luvisluvproject.domain.report.entity.ReportReason;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequestDto {
	private Long reportedId;
	private ReportReason reason;
	private String description;
}
