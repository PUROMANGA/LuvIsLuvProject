package com.example.luvisluvproject.domain.report.controller;

import com.example.luvisluvproject.domain.report.dto.ReportRequestDto;
import com.example.luvisluvproject.domain.report.dto.ReportResponseDto;
import com.example.luvisluvproject.domain.report.service.ReportService;
import com.example.luvisluvproject.global.common.AuthUser;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
	 * - 로그인한 사용자 정보는 AuthUser로부터 추출
	 * - 이메일 기반으로 내부 로직 처리
	 * - 성공 응답은 SuccessCode만 포함된 ApiResponse 형식
	 */
	@PostMapping
	public ResponseEntity<ApiResponse<ReportResponseDto>> report(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody ReportRequestDto dto
	) {
		ReportResponseDto response = reportService.report(authUser.getMember().getEmail(), dto);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.CREATE_REPORT_SUCCESS, response));
	}
}
