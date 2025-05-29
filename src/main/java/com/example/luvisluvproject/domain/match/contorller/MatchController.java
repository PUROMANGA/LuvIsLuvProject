package com.example.luvisluvproject.domain.match.contorller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	/**
	 * 매칭을 걸면 해당 사람에게 요청이 갑니다.
	 * @param matchRequestDto
	 * @param member
	 * @return
	 */

	@PostMapping
	public ResponseEntity<MatchResponseDto> createMatch(
		@RequestBody @Validated MatchRequestDto matchRequestDto,
		@AuthenticationPrincipal Member member) {
		return ResponseEntity.ok(matchService.createMatchService(matchRequestDto, member.getEmail()));
	}

	/**
	 * 해당 매칭이 오고, 매칭의 상태를 '받음'으로 변경합니다.
	 * @param matchId
	 * @param member
	 * @return
	 */

	@PatchMapping("/{matchId}")
	public ResponseEntity<MatchResponseDto> patchMatch(
		@PathVariable Long matchId,
		@AuthenticationPrincipal Member member) {
		return ResponseEntity.ok(matchService.patchMatchService(matchId, member.getEmail()));
	}
}
