package com.example.luvisluvproject.domain.report.controller;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.report.dto.ReportRequestDto;
import com.example.luvisluvproject.domain.report.dto.ReportResponseDto;
import com.example.luvisluvproject.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;

	@PostMapping
	public ResponseEntity<ReportResponseDto> report(
		@AuthenticationPrincipal Member member,
		@RequestBody ReportRequestDto dto
	) {
		return ResponseEntity.status(201).body(reportService.report(member.getId(), dto));
	}
}
