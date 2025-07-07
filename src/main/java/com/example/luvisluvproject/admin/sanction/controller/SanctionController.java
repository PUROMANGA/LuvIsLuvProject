package com.example.luvisluvproject.admin.sanction.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.admin.sanction.dto.SanctionReportDto;
import com.example.luvisluvproject.admin.sanction.dto.SanctionRequestDto;
import com.example.luvisluvproject.admin.sanction.dto.SanctionSuccessDto;
import com.example.luvisluvproject.admin.sanction.service.SanctionService;
import com.example.luvisluvproject.domain.member.dto.MemberInformationDto;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/sanctions")
public class SanctionController {

	private final SanctionService sanctionService;

	//member 보기
	@GetMapping("/members")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<Page<MemberInformationDto>>> getMembersSanctions(
		@PageableDefault(value = 10, size = 10, page = 0, sort = "creatTime", direction = Sort.Direction.DESC)Pageable pageable) {
		ApiResponse<Page<MemberInformationDto>> apiResponse = ApiResponse.of(SuccessCode.SUCCESS_OK,
			sanctionService.getMembersSanctionsService(pageable));
		return ResponseEntity.ok(apiResponse);
	}

	//report  보기
	@GetMapping("/reports/{memberId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<Page<SanctionReportDto>>> getSanctionReport(
		@PathVariable(value = "memberId") Long memberId,
		@PageableDefault(value = 10, size = 10, page = 0, sort = "creatTime", direction = Sort.Direction.DESC)Pageable pageable) {
		ApiResponse<Page<SanctionReportDto>> apiResponse = ApiResponse.of(SuccessCode.SUCCESS_OK,
			sanctionService.getSanctionReportService(memberId, pageable));
		return ResponseEntity.ok(apiResponse);
	}

	@PostMapping("/reports/{memberId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<SanctionSuccessDto>> createSanction(
		@PathVariable(value = "memberId") Long memberId,
		@RequestBody @Valid SanctionRequestDto sanctionRequestDto) {
		ApiResponse<SanctionSuccessDto> apiResponse = ApiResponse.of(SuccessCode.SUCCESS_OK,
			sanctionService.createSanctionService(memberId, sanctionRequestDto));
		return ResponseEntity.ok(apiResponse);
	}
}
