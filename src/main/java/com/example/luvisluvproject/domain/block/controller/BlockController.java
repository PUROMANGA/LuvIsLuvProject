package com.example.luvisluvproject.domain.block.controller;

import com.example.luvisluvproject.domain.block.dto.BlockRequestDto;
import com.example.luvisluvproject.domain.block.dto.BlockResponseDto;
import com.example.luvisluvproject.domain.block.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
public class BlockController {

	private final BlockService blockService;

	@PostMapping
	public ResponseEntity<BlockResponseDto> blockUser(@RequestHeader("X-User-Id") Long userId,
		@RequestBody BlockRequestDto requestDto) {
		return ResponseEntity.status(201).body(blockService.blockUser(userId, requestDto));
	}

	@DeleteMapping("/{blockedId}")
	public ResponseEntity<BlockResponseDto> unblockUser(@RequestHeader("X-User-Id") Long userId,
		@PathVariable Long blockedId) {
		return ResponseEntity.ok(blockService.unblockUser(userId, blockedId));
	}

	@GetMapping
	public ResponseEntity<List<BlockResponseDto>> getBlockedUsers(@RequestHeader("X-User-Id") Long userId) {
		return ResponseEntity.ok(blockService.getBlockedUsers(userId));
	}
}

// TODO: 현재는 임시로 사용자 ID를 헤더에서 받는 방식 (X-User-Id) 사용 중
// 추후 Spring Security 적용 시, @AuthenticationPrincipal 기반으로 변경 예정