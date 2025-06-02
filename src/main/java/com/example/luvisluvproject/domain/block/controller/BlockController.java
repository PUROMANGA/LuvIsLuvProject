package com.example.luvisluvproject.domain.block.controller;

import com.example.luvisluvproject.domain.block.dto.BlockRequestDto;
import com.example.luvisluvproject.domain.block.dto.BlockResponseDto;
import com.example.luvisluvproject.domain.block.service.BlockService;
import com.example.luvisluvproject.domain.member.entity.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blocks")
@RequiredArgsConstructor
public class BlockController {

	private final BlockService blockService;

	@PostMapping
	public ResponseEntity<BlockResponseDto> blockUser(
		@AuthenticationPrincipal Member member,
		@RequestBody BlockRequestDto requestDto
	) {
		BlockResponseDto response = blockService.blockUser(member.getId(), requestDto);
		return ResponseEntity.status(201).body(response);
	}

	@DeleteMapping("/{blockedId}")
	public ResponseEntity<String> unblockUser(
		@AuthenticationPrincipal Member member,
		@PathVariable Long blockedId
	) {
		String message = blockService.unblockUser(member.getId(), blockedId);
		return ResponseEntity.ok(message);
	}

	@GetMapping
	public ResponseEntity<List<String>> getBlockedUsers(
		@AuthenticationPrincipal Member member
	) {
		List<String> blockedUsers = blockService.getBlockedUsers(member.getId());
		return ResponseEntity.ok(blockedUsers);
	}
}
