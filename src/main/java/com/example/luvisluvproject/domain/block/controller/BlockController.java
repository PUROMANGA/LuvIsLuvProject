package com.example.luvisluvproject.domain.block.controller;

import java.util.List;

import com.example.luvisluvproject.domain.block.dto.BlockRequestDto;
import com.example.luvisluvproject.domain.block.dto.BlockResponseDto;
import com.example.luvisluvproject.domain.block.dto.BlockUserDto;
import com.example.luvisluvproject.domain.block.dto.UnblockResponseDto;
import com.example.luvisluvproject.domain.block.service.BlockService;
import com.example.luvisluvproject.global.common.AuthUser;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * BlockController
 * 사용자 차단 관련 요청을 처리하는 REST 컨트롤러
 */
@RestController
@RequestMapping("/blocks")
@RequiredArgsConstructor
public class BlockController {

	private final BlockService blockService;

	/**
	 * [POST] /blocks
	 * 사용자를 차단합니다.
	 * - 인증된 사용자 기준으로 차단 처리
	 * - HTTP Status는 항상 200 OK
	 * - 메시지 없이 SuccessCode로만 성공 판단
	 */
	@PostMapping
	public ResponseEntity<ApiResponse<BlockResponseDto>> blockUser(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody BlockRequestDto requestDto
	) {
		BlockResponseDto response = blockService.blockUser(authUser.getMember().getEmail(), requestDto);
		return ResponseEntity.status(201).body(ApiResponse.of(SuccessCode.BLOCK_USER_SUCCESS, response));
	}

	/**
	 * [DELETE] /blocks/{blockedId}
	 * 사용자를 차단 해제합니다.
	 * - 인증된 사용자 기준으로 차단 해제
	 */
	@DeleteMapping("/{blockedEmail}")
	public ResponseEntity<ApiResponse<UnblockResponseDto>> unblockUser(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable String blockedEmail
	) {
		UnblockResponseDto response = blockService.unblockUser(authUser.getMember().getEmail(),blockedEmail);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.UNBLOCK_USER_SUCCESS, response));
	}

	/**
	 * [GET] /blocks
	 * 차단한 사용자 목록 조회 (Slice 기반 페이징)
	 * - 일반 사용자는 본인 기준 차단 목록
	 * - 관리자만 다른 사용자(userId) 기준으로 차단 목록 조회 가능
	 *
	 * @param userId 관리자용 특정 사용자 ID
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/admin/{userId}")
	public ResponseEntity<ApiResponse<Slice<BlockUserDto>>> getUserBlockedUsers(
		@PathVariable Long userId,
		@PageableDefault(size = 20) Pageable pageable
	) {
		Slice<BlockUserDto> blockedUsers = blockService.getBlockedUsersByUserId(userId, pageable);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.GET_BLOCKED_USERS_SUCCESS, blockedUsers));
	}

	/**
	 * [GET] /blocks/my
	 * ✅ 일반 사용자용 API
	 * 로그인한 사용자의 이메일을 기준으로 차단한 사용자 목록을 Slice 방식으로 조회합니다.
	 * - @AuthenticationPrincipal로 인증된 AuthUser의 이메일을 추출해 사용
	 * - Slice 페이징 처리로 무한 스크롤 대응 가능
	 */
	@GetMapping("/my")
	public ResponseEntity<ApiResponse<Slice<BlockUserDto>>> getMyBlockedUsers(
		@AuthenticationPrincipal AuthUser authUser,
		@PageableDefault(size = 20) Pageable pageable
	) {
		// 본인 이메일을 기준으로 차단 목록 조회
		Slice<BlockUserDto> blockedUsers = blockService.getBlockedUsersByEmail(
			authUser.getMember().getEmail(), pageable
		);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.GET_BLOCKED_USERS_SUCCESS, blockedUsers));
	}

}
