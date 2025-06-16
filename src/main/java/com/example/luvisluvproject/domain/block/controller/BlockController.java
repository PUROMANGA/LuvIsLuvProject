package com.example.luvisluvproject.domain.block.controller;

import com.example.luvisluvproject.domain.block.dto.BlockRequestDto;
import com.example.luvisluvproject.domain.block.dto.BlockResponseDto;
import com.example.luvisluvproject.domain.block.dto.BlockUserDto;
import com.example.luvisluvproject.domain.block.dto.UnblockResponseDto;
import com.example.luvisluvproject.domain.block.service.BlockService;
import com.example.luvisluvproject.domain.member.entity.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import java.util.List;

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
	 * 사용자를 차단합니다. //어스유저
	 */
	@PostMapping
	public ResponseEntity<ApiResponse<BlockResponseDto>> blockUser(
		@AuthenticationPrincipal Member member,
		@RequestBody BlockRequestDto requestDto
	) {
		BlockResponseDto response = blockService.blockUser(member.getId(), requestDto);
		return ResponseEntity.status(201).body(ApiResponse.of(SuccessCode.BLOCK_USER_SUCCESS, response));
	}

	/**
	 * [DELETE] /blocks/{blockedId}
	 * 사용자를 차단 해제합니다.
	 */
	@DeleteMapping("/{blockedId}")
	public ResponseEntity<ApiResponse<UnblockResponseDto>> unblockUser(
		@AuthenticationPrincipal Member member,
		@PathVariable Long blockedId
	) {
		UnblockResponseDto response = blockService.unblockUser(member.getId(), blockedId);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.UNBLOCK_USER_SUCCESS, response));
	}

	/**
	 * [GET] /blocks
	 * 내가 차단한 사용자 목록을 조회합니다.
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<List<BlockUserDto>>> getBlockedUsers(
		@AuthenticationPrincipal Member member
	) {
		List<BlockUserDto> blockedUsers = blockService.getBlockedUsers(member.getId());
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.GET_BLOCKED_USERS_SUCCESS, blockedUsers));
	}
}
