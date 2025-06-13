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
import com.example.luvisluvproject.domain.member.dto.MemberMyProfileResponse;
import com.example.luvisluvproject.domain.member.dto.MemberUpdateContentRequest;
import com.example.luvisluvproject.domain.member.dto.MemberUpdatePasswordRequest;
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
	 * 로그인한 사용자의 프로필 정보를 조회합니다.
	 * 요청을 보낸 사용자의 인증 정보를 바탕으로,
	 * 해당 사용자의 아이디, 이름, 이메일, 나이, 소개글 프로필 정보를 조회하여 반환합니다.
	 *
	 * @param authUser 인증된 사용자 정보 (Spring Security의 @AuthenticationPrincipal을 통해 주입됨)
	 * @return 사용자 프로필 정보와 함께 200 OK 응답을 반환
	 * (응답 본문은 {@link MemberMyProfileResponse}를 포함한 {@link ApiResponse})
	 */
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<MemberMyProfileResponse>> findMyInfo(
		@AuthenticationPrincipal AuthUser authUser
	) {
		MemberMyProfileResponse profileResponse = memberService.getMyProfile(authUser.getId());
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.FIND_MEMBER_SUCCESS, profileResponse));
	}

	/**
	 * 특정 회원의 프로필 정보를 조회합니다.
	 * 로그인한 사용자(viewer)가 요청한 memberId에 해당하는 회원의 프로필을 조회합니다.
	 * 조회 대상 회원과 로그인한 사용자 간에 차단 관계가 존재하면 접근이 거부됩니다.
	 *
	 * @param viewer 현재 인증된 로그인 사용자 (조회자)
	 * @param memberId 조회할 회원의 고유 ID
	 * @return 조회 성공 시 {@code 200 OK}와 함께 회원 프로필 정보가 담긴 {@link ApiResponse}를 반환합니다.
	 * @throws CustomRuntimeException {@link ExceptionCode#PROFILE_ACCESS_DENIED} 로그인 사용자와 조회 대상 회원 간 차단 관계가 있을 경우 발생
	 * @throws CustomRuntimeException {@link ExceptionCode#MEMBER_NOT_FOUND} 조회 대상 회원이 없거나 비활성 상태일 경우 발생
	 */
	@GetMapping("/{memberId}/profile")
	public ResponseEntity<ApiResponse<MemberFindResponse>> getProfile(
		@AuthenticationPrincipal Member viewer,
		@PathVariable Long memberId
	) {
		Member targetMember = memberService.getMemberEntityById(memberId);

		if (blockService.isProfileBlocked(viewer, targetMember)) {
			throw new CustomRuntimeException(ExceptionCode.PROFILE_ACCESS_DENIED);
		}

		MemberFindResponse profile = new MemberFindResponse(
			targetMember.getId(),
			targetMember.getName(),
			targetMember.getBirthday(),
			targetMember.getContent()
		);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.FIND_MEMBER_SUCCESS, profile));
	}

	/**
	 * 로그인한 사용자의 비밀번호를 수정합니다.
	 *
	 * @param authUser 인증된 사용자 정보
	 * @param memberUpdatePasswordRequest 수정할 비밀번호 요청 정보
	 * @return 수정 성공 응답 (200 OK)
	 */
	@PutMapping("/me/password")
	public ResponseEntity<ApiResponse<Void>> updateMyInfo(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid MemberUpdatePasswordRequest memberUpdatePasswordRequest
	) {
		memberService.updatePasswordMember(authUser.getId(), memberUpdatePasswordRequest);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.UPDATE_MEMBER_SUCCESS, null));
	}

	/**
	 * 로그인한 사용자의 자기소개(content)를 수정합니다.
	 *
	 * @param authUser 인증된 사용자 정보
	 * @param memberUpdateContentRequest 수정할 자기소개 요청 정보
	 * @return 수정 성공 응답 (200 OK)
	 */
	@PutMapping("/me/content")
	public ResponseEntity<ApiResponse<Void>> updateMyInfo(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid MemberUpdateContentRequest memberUpdateContentRequest
	) {
		memberService.updateContentMember(authUser.getId(), memberUpdateContentRequest);
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
