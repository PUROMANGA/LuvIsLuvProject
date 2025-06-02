package com.example.luvisluvproject.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.domain.member.dto.MemberDeleteRequest;
import com.example.luvisluvproject.domain.member.dto.MemberFindResponse;
import com.example.luvisluvproject.domain.member.dto.MemberUpdateRequest;
import com.example.luvisluvproject.domain.member.service.MemberService;
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

	/**
	 * 회원 ID를 통해 회원 정보를 조회합니다.
	 * 요청된 회원 ID를 기반으로 회원 정보를 조회하며, 정상적으로 조회되면
	 * {@link SuccessCode#FIND_MEMBER_SUCCESS}와 함께 응답합니다.
	 * 회원이 존재하지 않거나 삭제된 경우 {@link CustomRuntimeException}이 발생합니다.
	 * @param memberId 조회할 회원의 ID (Path Variable)
	 * @return 회원 정보와 응답 코드가 포함된 {@link ResponseEntity} 객체
	 * @throws CustomRuntimeException {@link ExceptionCode#MEMBER_NOT_FOUND} - 회원이 존재하지 않거나 삭제된 경우
	 */
	@GetMapping("/{memberId}")
	public ResponseEntity<ApiResponse<MemberFindResponse>> findById(
		@PathVariable Long memberId
	) {
		MemberFindResponse memberResponse = memberService.findById(memberId);

		ApiResponse<MemberFindResponse> response = ApiResponse.of(SuccessCode.FIND_MEMBER_SUCCESS, memberResponse);

		return ResponseEntity.ok(response);
	}

	/**
	 * 회원 비밀번호를 수정합니다.
	 * 요청된 회원 ID를 기반으로 회원을 조회하고, 전달받은 기존 비밀번호가 일치하는 경우
	 * 새 비밀번호로 변경합니다. 변경이 완료되면 {@link SuccessCode#UPDATE_MEMBER_SUCCESS} 코드와 함께
	 * 200 OK 응답을 반환합니다.
	 * @param memberId 수정할 회원의 ID (Path Variable)
	 * @param memberUpdateRequest 비밀번호 수정 요청 정보 (기존 비밀번호 및 새 비밀번호 포함)
	 * @return 비어 있는 본문과 성공 응답 코드를 포함한 {@link ResponseEntity}
	 * @throws CustomRuntimeException {@link ExceptionCode#MEMBER_NOT_FOUND} - 회원이 존재하지 않을 경우
	 * @throws CustomRuntimeException {@link ExceptionCode#PASSWORD_MISMATCH} - 기존 비밀번호가 일치하지 않을 경우
	 * @throws CustomRuntimeException {@link ExceptionCode#SAME_PASSWORD} - 기존 비밀번호와 새 비밀번호가 동일한 경우
	 */
	@PutMapping("/{memberId}")
	public ResponseEntity<ApiResponse<Void>> updateMember(
		@PathVariable Long memberId,
		@RequestBody @Valid MemberUpdateRequest memberUpdateRequest
	) {
		memberService.updateMember(memberId, memberUpdateRequest);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.UPDATE_MEMBER_SUCCESS, null));
	}

	/**
	 * 회원을 소프트 딜리트(Soft Delete) 방식으로 삭제합니다.
	 * 요청된 회원 ID로 회원을 조회한 뒤, 요청 본문에 포함된 비밀번호가 일치하면
	 * 해당 회원의 상태(status)를 변경하여 논리적으로 삭제 처리합니다.
	 * 삭제가 완료되면 {@link SuccessCode#DELETE_MEMBER_SUCCESS} 코드와 함께 200 OK 응답을 반환합니다.
	 * @param memberId 삭제할 회원의 ID (Path Variable)
	 * @param memberDeleteRequest 삭제 요청 정보 (비밀번호 포함)
	 * @return 비어 있는 본문과 성공 응답 코드를 포함한 {@link ResponseEntity}
	 * @throws CustomRuntimeException {@link ExceptionCode#MEMBER_NOT_FOUND} - 회원이 존재하지 않을 경우
	 * @throws CustomRuntimeException {@link ExceptionCode#PASSWORD_MISMATCH} - 비밀번호가 일치하지 않을 경우
	 */
	@DeleteMapping("/{memberId}")
	public ResponseEntity<ApiResponse<Void>> deleteMember(
		@PathVariable Long memberId,
		@RequestBody @Valid MemberDeleteRequest memberDeleteRequest
	) {
		memberService.deleteMember(memberId, memberDeleteRequest);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.DELETE_MEMBER_SUCCESS, null));
	}

}
