package com.example.luvisluvproject.domain.memberInteractionLog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.domain.memberInteractionLog.common.MemberInteractionLogDtoFactory;
import com.example.luvisluvproject.domain.memberInteractionLog.dto.MemberInteractionLogDto;
import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberInteractionLog;
import com.example.luvisluvproject.domain.memberInteractionLog.repository.MemberInteractionLogRepository;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberInteractionLogController {

	private final MemberInteractionLogRepository memberInteractionLogRepository;
	private final MemberInteractionLogDtoFactory memberInteractionLogDtoFactory;

	@GetMapping("/memberInteractionLogs")
	public ResponseEntity<ApiResponse<MemberInteractionLogDto>> getMemberInteractionLog(@RequestParam Long memberId) {

		MemberInteractionLog memberInteractionLog = memberInteractionLogRepository.findById(memberId).orElseThrow(() -> new RuntimeException("로그를 수집하지 못하였습니다."));
		MemberInteractionLogDto memberInteractionLogDto = memberInteractionLogDtoFactory.memberInteractionLogDtoOf(memberInteractionLog);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.SUCCESS_OK, memberInteractionLogDto));
	}
}
