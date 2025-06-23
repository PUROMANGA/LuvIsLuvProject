package com.example.luvisluvproject.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.domain.block.service.BlockService;
import com.example.luvisluvproject.domain.member.dto.MemberDeleteRequest;
import com.example.luvisluvproject.domain.member.dto.MemberFindResponse;
import com.example.luvisluvproject.domain.member.dto.MemberMyProfileResponse;
import com.example.luvisluvproject.domain.member.dto.MemberUpdateProfile;
import com.example.luvisluvproject.domain.member.dto.MemberPasswordRequest;
import com.example.luvisluvproject.domain.member.service.MemberService;
import com.example.luvisluvproject.global.common.AuthUser;
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
	 * 로그인한 사용자의 프로필 정보를 조회합니다.
	 * 요청을 보낸 사용자의 인증 정보를 바탕으로,
	 * 해당 사용자의 아이디, 이름, 이메일, 나이, 소개글 프로필 정보를 조회하여 반환합니다.
	 *
	 * @param authUser 인증된 사용자 정보 (Spring Security의 @AuthenticationPrincipal을 통해 주입됨)
	 * @return 사용자 프로필 정보와 함께 200 OK 응답을 반환
	 * (응답 본문은 {@link MemberMyProfileResponse}를 포함한 {@link ApiResponse})
	 */
	@GetMapping("/me/profile")
	public ResponseEntity<ApiResponse<MemberMyProfileResponse>> findMyInfo(
		@AuthenticationPrincipal AuthUser authUser) {
		MemberMyProfileResponse profileResponse = memberService.getMyProfile(authUser.getUsername());
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.FIND_MEMBER_SUCCESS, profileResponse));
	}

	/**
	 * 지정된 회원의 프로필 정보를 조회합니다.
	 * 현재 로그인한 회원(authUser)이 요청 대상 회원(memberId)의 프로필을 차단한 경우 예외가 발생합니다.
	 */
	@GetMapping("/{memberId}/profile")
	public ResponseEntity<ApiResponse<MemberFindResponse>> getProfile(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long memberId) {
		MemberFindResponse profile = memberService.getMemberProfile(authUser.getUsername(), memberId);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.FIND_MEMBER_SUCCESS, profile));
	}

	/**
	 * 회원정보를 수정하기 전 비밀번호를 입력하게 만들고, db의 비밀번호랑 비교해서 체크합니다.
	 *
	 * @param authUser 인증된 사용자 정보
	 * @param memberUpdatePasswordRequest 수정할 비밀번호 요청 정보
	 * @return 수정 성공 응답 (200 OK)
	 */
	@PostMapping("/me/profile")
	public ResponseEntity<ApiResponse<Void>> updateMyInfo(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid MemberPasswordRequest memberPasswordRequest) {
		memberService.checkPasswordMember(authUser.getUsername(), memberPasswordRequest);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.SUCCESS_OK, null));
	}

	/**
	 * 로그인한 사용자의 프로필을 수정합니다.
	 */
	@PatchMapping("/me/profile")
	public ResponseEntity<ApiResponse<Void>> updateMyInfo(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid MemberUpdateProfile memberUpdateProfile) {
		memberService.updateMemberProfile(authUser.getUsername(), memberUpdateProfile);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.UPDATE_MEMBER_SUCCESS, null));
	}

	/**
	 * 현재 로그인한 사용자의 회원 정보를 소프트 삭제 처리합니다.
	 * 인증된 사용자 정보(authUser)로부터 회원 ID를 추출하여 요청된
	 */
	@DeleteMapping("/me")
	public ResponseEntity<ApiResponse<Void>> deleteMyInfo(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid MemberDeleteRequest memberDeleteRequest) {
		memberService.deleteMember(authUser.getUsername(), memberDeleteRequest);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.DELETE_MEMBER_SUCCESS, null));
	}

}
