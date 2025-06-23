package com.example.luvisluvproject.domain.block.controller;

import static org.springframework.data.domain.Sort.Direction.*;

import com.example.luvisluvproject.domain.block.dto.BlockRequestDto;
import com.example.luvisluvproject.domain.block.dto.BlockResponseDto;
import com.example.luvisluvproject.domain.block.dto.BlockUserDto;
import com.example.luvisluvproject.domain.block.service.BlockService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.luvisluvproject.global.common.AuthUser;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

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
		@AuthenticationPrincipal AuthUser member,
		@RequestBody BlockRequestDto requestDto) {
		BlockResponseDto response = blockService.blockUser(member.getUsername(), requestDto);
		ApiResponse<BlockResponseDto> apiResponse = ApiResponse.of(SuccessCode.BLOCK_USER_SUCCESS, response);
		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * [DELETE] /blocks/{userId}
	 * blockedId와 userId는 같은 맥락이지만 userId인 편이 가시성이 좋다고 생각하여 바꿈.
	 * 사용자를 차단 해제합니다.
	 */
	@DeleteMapping("/{userId}")
	public ResponseEntity<ApiResponse<Void>> unblockUser(
		@AuthenticationPrincipal AuthUser member,
		@PathVariable Long userId) {
		blockService.unblockUser(member.getUsername(), userId);
		ApiResponse<Void> apiResponse = ApiResponse.of(SuccessCode.UNBLOCK_USER_SUCCESS, null);
		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * [GET] /blocks
	 * 내가 차단한 사용자 목록을 조회합니다.
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<Slice<BlockUserDto>>> getBlockedUsers(
		@AuthenticationPrincipal AuthUser member,
		@PageableDefault(size = 10, sort = "creatTime", direction = DESC) Pageable pageable) {
		ApiResponse<Slice<BlockUserDto>> apiResponse = ApiResponse.of(SuccessCode.UNBLOCK_USER_SUCCESS, blockService.getBlockedUsers(member.getUsername(), pageable));
		return ResponseEntity.ok(apiResponse);
	}
}
