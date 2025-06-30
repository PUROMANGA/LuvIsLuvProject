package com.example.luvisluvproject.domain.notify.controller;

import static org.springframework.data.domain.Sort.Direction.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.domain.notify.dto.NotifyDto;
import com.example.luvisluvproject.domain.notify.service.NotifyService;
import com.example.luvisluvproject.global.common.AuthUser;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifies")
public class NotifyController {

	private final NotifyService notifyService;

	/**
	 * 내가 받았던 알람을 Slice방식으로 다 가져옵니다.
	 * @param authUser
	 * @param pageable
	 * @return
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<Slice<NotifyDto>>> getNotify(
		@AuthenticationPrincipal AuthUser authUser,
		@PageableDefault(size = 10, sort = "creatTime", direction = DESC)Pageable pageable) {
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.SUCCESS_OK, notifyService.getNotifyService(authUser.getUsername(), pageable)));
	}
}
