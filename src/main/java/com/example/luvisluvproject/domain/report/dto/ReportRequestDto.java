package com.example.luvisluvproject.domain.report.dto;

import com.example.luvisluvproject.domain.report.entity.ReportReason;
import com.example.luvisluvproject.domain.report.entity.ReportTargetType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequestDto {
	private ReportTargetType targetType;
	private Long targetId;
	private ReportReason reason;
	private String description;
}
