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
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/{memberId}")
	public ResponseEntity<ApiResponse<MemberFindResponse>> findById(
		@PathVariable("memberId") Long id
	) {
		MemberFindResponse memberResponse = memberService.findById(id);

		ApiResponse<MemberFindResponse> response = ApiResponse.of(SuccessCode.FIND_MEMBER_SUCCESS, memberResponse);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{memberId}")
	public ResponseEntity<ApiResponse<Void>> updateMember(
		@PathVariable("memberId") Long id,
		@RequestBody @Valid MemberUpdateRequest memberUpdateRequest
	) {
		memberService.updateMember(id, memberUpdateRequest);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.UPDATE_MEMBER_SUCCESS));
	}

	@DeleteMapping("/{memberId}")
	public ResponseEntity<ApiResponse<Void>> deleteMember(
		@PathVariable("memberId") Long id,
		@RequestBody @Valid MemberDeleteRequest memberDeleteRequest
	) {
		memberService.deleteMember(id, memberDeleteRequest);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.DELETE_MEMBER_SUCCESS));
	}

}
