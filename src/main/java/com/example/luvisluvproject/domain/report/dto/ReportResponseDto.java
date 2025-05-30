package com.example.luvisluvproject.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReportResponseDto {
	private String message;
	private LocalDateTime createdAt;
}
