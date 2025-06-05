package com.example.luvisluvproject.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.domain.block.service.BlockService;
import com.example.luvisluvproject.domain.member.dto.MemberDeleteRequest;
import com.example.luvisluvproject.domain.member.dto.MemberFindResponse;
import com.example.luvisluvproject.domain.member.dto.MemberUpdateRequest;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.service.MemberService;
import com.example.luvisluvproject.global.common.AuthUser;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final BlockService blockService;

	/**
	 * 현재 로그인한 사용자의 회원 정보를 조회합니다.
	 * 인증된 사용자 정보(authUser)로부터 회원 ID를 추출하여 해당 회원 정보를 조회합니다.
	 * 회원 정보는 {@link MemberFindResponse} 형태로 반환됩니다.
	 * @param authUser 인증된 사용자의 {@link AuthUser} 객체
	 * @return 조회된 회원 정보를 담은 {@link ApiResponse} 객체와 함께 HTTP 200 상태 반환
	 */
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<MemberFindResponse>> findMyInfo(
		@AuthenticationPrincipal AuthUser authUser
	) {
		MemberFindResponse memberResponse = memberService.findById(authUser.getId());
		ApiResponse<MemberFindResponse> response = ApiResponse.of(SuccessCode.FIND_MEMBER_SUCCESS, memberResponse);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{memberId}/profile")
	public ResponseEntity<ApiResponse<MemberFindResponse>> getProfile(
		@AuthenticationPrincipal Member viewer, // 로그인한 사용자
		@PathVariable Long memberId
	) {
		MemberFindResponse profile = memberService.findById(memberId);

		if (blockService.isProfileBlocked(viewer, profile.getMember())) {
			throw new CustomRuntimeException(ExceptionCode.PROFILE_ACCESS_DENIED);
		}

		ApiResponse<MemberFindResponse> response = ApiResponse.of(SuccessCode.FIND_MEMBER_SUCCESS, profile);
		return ResponseEntity.ok(response);
	}

	/**
	 * 현재 로그인한 사용자의 회원 정보를 조회합니다.
	 * 인증된 사용자 정보(authUser)로부터 회원 ID를 추출하여 해당 회원 정보를 조회합니다.
	 * 회원 정보는 {@link MemberFindResponse} 형태로 반환됩니다.
	 * @param authUser 인증된 사용자의 {@link AuthUser} 객체
	 * @return 조회된 회원 정보를 담은 {@link ApiResponse} 객체와 함께 HTTP 200 상태 반환
	 */
	@PutMapping("/me")
	public ResponseEntity<ApiResponse<Void>> updateMyInfo(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid MemberUpdateRequest memberUpdateRequest
	) {
		memberService.updateMember(authUser.getId(), memberUpdateRequest);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.UPDATE_MEMBER_SUCCESS, null));
	}

	/**
	 * 현재 로그인한 사용자의 회원 정보를 소프트 삭제 처리합니다.
	 * 인증된 사용자 정보(authUser)로부터 회원 ID를 추출하여 요청된
	 * {@link MemberDeleteRequest}의 비밀번호 검증 후, 회원 상태를 변경하여 논리적 삭제를 수행합니다.
	 * @param authUser 인증된 사용자의 {@link AuthUser} 객체
	 * @param memberDeleteRequest 회원 삭제 요청 데이터 (비밀번호 포함)
	 * @return 성공 메시지를 담은 {@link ApiResponse} 객체와 함께 HTTP 200 상태 반환
	 */
	@DeleteMapping("/me")
	public ResponseEntity<ApiResponse<Void>> deleteMyInfo(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid MemberDeleteRequest memberDeleteRequest
	) {
		memberService.deleteMember(authUser.getId(), memberDeleteRequest);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.DELETE_MEMBER_SUCCESS, null));
	}

}
