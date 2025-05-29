package com.example.luvisluvproject.domain.match.contorller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.domain.match.Service.MatchService;
import com.example.luvisluvproject.domain.match.dto.MatchRequestDto;
import com.example.luvisluvproject.domain.match.dto.MatchResponseDto;
import com.example.luvisluvproject.domain.member.entity.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matches")

public class MatchController {
	private final MatchService matchService;

	@PostMapping
	public ResponseEntity<MatchResponseDto> createMatch(
		@RequestBody @Validated MatchRequestDto matchRequestDto,
		@AuthenticationPrincipal Member member) {
		return ResponseEntity.ok(matchService.createMatchService(matchRequestDto, member.getEmail()));
	}
}
