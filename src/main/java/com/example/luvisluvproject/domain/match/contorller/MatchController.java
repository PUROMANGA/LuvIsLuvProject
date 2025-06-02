package com.example.luvisluvproject.domain.match.contorller;

import static org.springframework.data.domain.Sort.Direction.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.domain.match.service.MatchService;
import com.example.luvisluvproject.domain.match.dto.AcceptMatchDto;
import com.example.luvisluvproject.domain.match.dto.MatchResponseDto;
import com.example.luvisluvproject.domain.member.entity.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matches")

public class MatchController {
	private final MatchService matchService;

	/**
	 * 내(member)가 원하는 상대방(receiverId)에게 매칭을 신청합니다.
	 * @param receiverId
	 * @param member
	 * @return
	 */

	@PostMapping
	public ResponseEntity<MatchResponseDto> createMatch(
		@RequestParam Long receiverId,
		@AuthenticationPrincipal Member member) {
		return ResponseEntity.ok(matchService.createMatchService(receiverId, member.getEmail()));
	}

	/**
	 * 해당 매칭이 오고, 매칭의 상태를 '수락' 혹은 '거절'로 변경합니다. 이때 senderId는 현재 로그인된 멤버 자기 자신의 ID입니다.
	 * @param matchId
	 * @param acceptMatchDto
	 * @param member
	 * @return
	 */
	@PatchMapping("/{matchId}")
	public ResponseEntity<MatchResponseDto> patchMatch(
		@PathVariable Long matchId,
		@RequestBody @Validated AcceptMatchDto acceptMatchDto,
		@AuthenticationPrincipal Member member) {
		return ResponseEntity.ok(matchService.patchMatchService(matchId, acceptMatchDto, member.getEmail()));
	}

	/**
	 * 내가 받은 match를 전부 SLICE로 가져옵니다.
	 * @param member
	 * @param pageable
	 * @return
	 */

	@GetMapping
	public ResponseEntity<Slice<MatchResponseDto>> getMatch(
		@AuthenticationPrincipal Member member,
		@PageableDefault(size = 10, sort = "creatTime", direction = DESC) Pageable pageable) {
		return ResponseEntity.ok(matchService.getMatchService(member.getEmail(), pageable));
	}
}
