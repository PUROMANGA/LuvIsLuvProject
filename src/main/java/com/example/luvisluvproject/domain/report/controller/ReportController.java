package com.example.luvisluvproject.domain.report.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.domain.report.dto.ReportRequestDto;
import com.example.luvisluvproject.domain.report.dto.ReportResponseDto;
import com.example.luvisluvproject.domain.report.service.ReportService;
import com.example.luvisluvproject.global.common.AuthUser;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import lombok.RequiredArgsConstructor;

/**
 * ReportController
 * 신고 요청을 처리하는 REST 컨트롤러입니다.
 */
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;

	/**
	 * [POST] /reports
	 * 신고를 생성합니다.
	 *
	 * @param member 로그인한 사용자
	 * @param dto 신고 요청 정보
	 * @return 신고 응답 정보
	 */
	@PostMapping
	public ResponseEntity<ApiResponse<ReportResponseDto>> report(
		@AuthenticationPrincipal AuthUser member,
		@RequestBody ReportRequestDto dto) {
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.CREATE_REPORT_SUCCESS,
			reportService.report(member.getUsername(), dto)));
	}
}