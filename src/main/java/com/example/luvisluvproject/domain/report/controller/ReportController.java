package com.example.luvisluvproject.domain.report.controller;

import com.example.luvisluvproject.domain.report.dto.ReportRequestDto;
import com.example.luvisluvproject.domain.report.dto.ReportResponseDto;
import com.example.luvisluvproject.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;

	@PostMapping("/users")
	public ResponseEntity<ReportResponseDto> reportUser(@RequestHeader("X-User-Id") Long userId,
		@RequestBody ReportRequestDto dto) {
		return ResponseEntity.status(201).body(reportService.reportUser(userId, dto));
	}
}

// TODO: X-User-Id는 추후 @AuthenticationPrincipal 기반 인증으로 변경 예정