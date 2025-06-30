package com.example.luvisluvproject.domain.match.contorller;

import static org.springframework.data.domain.Sort.Direction.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.domain.match.dto.AcceptMatchDto;
import com.example.luvisluvproject.domain.match.dto.MatchResponseDto;
import com.example.luvisluvproject.domain.match.dto.ResponseMatchMemberDto;
import com.example.luvisluvproject.domain.match.service.MatchService;
import com.example.luvisluvproject.global.common.AuthUser;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/matches")

public class MatchController {
	private final MatchService matchService;
	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * 매칭을 해줍니다
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<List<ResponseMatchMemberDto>>> getMatchMemberList(
		@AuthenticationPrincipal AuthUser member) {
		ApiResponse apiResponse = ApiResponse.of(SuccessCode.SUCCESS_OK,
			matchService.getMatchMemberListService(member.getUsername()));
		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * 내(member)가 원하는 상대방(receiverId)에게 매칭을 신청합니다.
	 * @param receiverId
	 * @param member
	 * @return
	 */
	@PostMapping
	public ResponseEntity<ApiResponse<MatchResponseDto>> createMatch(
		@RequestParam Long receiverId,
		@AuthenticationPrincipal AuthUser member) {
		ApiResponse apiResponse = ApiResponse.of(SuccessCode.SUCCESS_OK,
			matchService.createMatchService(receiverId, member.getUsername()));
		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * 해당 매칭이 오고, 매칭의 상태를 '수락' 혹은 '거절'로 변경합니다. 이때 senderId는 현재 로그인된 멤버 자기 자신의 ID입니다.
	 * @param senderId
	 * @param acceptMatchDto
	 * @param member
	 * @return
	 */
	@PatchMapping("/{senderId}")
	public ResponseEntity<ApiResponse<MatchResponseDto>> patchMatch(
		@PathVariable Long senderId,
		@RequestBody @Valid AcceptMatchDto acceptMatchDto,
		@AuthenticationPrincipal AuthUser member) {
		ApiResponse apiResponse = ApiResponse.of(SuccessCode.SUCCESS_OK,
			matchService.patchMatchService(senderId, acceptMatchDto, member.getUsername()));
		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * 내가 받은 match를 전부 SLICE로 가져옵니다.
	 * @param member
	 * @param pageable
	 * @return
	 */
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<Slice<MatchResponseDto>>> getMatch(
		@AuthenticationPrincipal AuthUser member,
		@PageableDefault(size = 10, sort = "creatTime", direction = DESC) Pageable pageable) {
		ApiResponse apiResponse = ApiResponse.of(SuccessCode.SUCCESS_OK,
			matchService.getMatchService(member.getUsername(), pageable));
		return ResponseEntity.ok(apiResponse);
	}
}
