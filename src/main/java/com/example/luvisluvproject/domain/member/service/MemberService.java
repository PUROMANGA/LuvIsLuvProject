package com.example.luvisluvproject.domain.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.member.dto.MemberDeleteRequest;
import com.example.luvisluvproject.domain.member.dto.MemberFindResponse;
import com.example.luvisluvproject.domain.member.dto.MemberUpdateRequest;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * 주어진 회원 ID로 회원 정보를 조회합니다.
	 * 회원이 존재하지 않거나, 소프트 딜리트된(status=true) 경우 {@link CustomRuntimeException}이 발생합니다.
	 * @param memberId 조회할 회원의 ID
	 * @return 조회된 회원의 정보를 담은 {@link MemberFindResponse}
	 * @throws CustomRuntimeException {@link ExceptionCode#MEMBER_NOT_FOUND} - 회원이 존재하지 않거나 삭제된 경우
	 */
	@Transactional(readOnly = true)
	public MemberFindResponse findById(Long memberId) {

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));

		if (member.isStatus()) {
			throw new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND);
		}

		return new MemberFindResponse(member.getId(),
			member.getName(),
			member.getEmail(),
			member.getBirthday()
		);
	}

	/**
	 * 회원의 비밀번호를 수정합니다.
	 * 주어진 회원 ID로 회원을 조회하고, 요청된 이전 비밀번호가 일치하는 경우 새 비밀번호로 변경합니다.
	 * 이전 비밀번호와 새 비밀번호가 같을 경우 {@link ExceptionCode#SAME_PASSWORD} 예외가 발생하며,
	 * 비밀번호가 일치하지 않을 경우 {@link ExceptionCode#PASSWORD_MISMATCH} 예외가 발생합니다.
	 * @param memberId 수정할 회원의 ID
	 * @param memberUpdateRequest 수정 요청 정보 (기존 비밀번호와 새 비밀번호 포함)
	 * @throws CustomRuntimeException {@link ExceptionCode#MEMBER_NOT_FOUND} - 회원이 존재하지 않을 경우
	 * @throws CustomRuntimeException {@link ExceptionCode#PASSWORD_MISMATCH} - 기존 비밀번호가 일치하지 않을 경우
	 * @throws CustomRuntimeException {@link ExceptionCode#SAME_PASSWORD} - 기존 비밀번호와 새 비밀번호가 같은 경우
	 */
	@Transactional
	public void updateMember(Long memberId, MemberUpdateRequest memberUpdateRequest) {

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));

		if (!passwordEncoder.matches(memberUpdateRequest.getOldPassword(), member.getPassword())) {
			throw new CustomRuntimeException(ExceptionCode.PASSWORD_MISMATCH);
		}
		if (memberUpdateRequest.getOldPassword().equals(memberUpdateRequest.getNewPassword())) {
			throw new CustomRuntimeException(ExceptionCode.SAME_PASSWORD);
		}
		member.update(passwordEncoder.encode(memberUpdateRequest.getNewPassword()));
	}

	/**
	 * 회원을 소프트 딜리트(Soft Delete) 처리합니다.
	 * 주어진 회원 ID로 회원을 조회한 뒤, 요청된 비밀번호가 일치하는 경우
	 * 해당 회원의 상태(status)를 true로 변경하여 논리적 삭제를 수행합니다.
	 * 비밀번호가 일치하지 않으면 {@link ExceptionCode#PASSWORD_MISMATCH} 예외가 발생합니다.
	 * @param memberId 삭제할 회원의 ID
	 * @param memberDeleteRequest 삭제 요청 정보 (비밀번호 포함)
	 * @throws CustomRuntimeException {@link ExceptionCode#MEMBER_NOT_FOUND} - 회원이 존재하지 않을 경우
	 * @throws CustomRuntimeException {@link ExceptionCode#PASSWORD_MISMATCH} - 비밀번호가 일치하지 않을 경우
	 */
	@Transactional
	public void deleteMember(Long memberId, @Valid MemberDeleteRequest memberDeleteRequest) {

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));

		if (!passwordEncoder.matches(memberDeleteRequest.getPassword(), member.getPassword())) {
			throw new CustomRuntimeException(ExceptionCode.PASSWORD_MISMATCH);
		}
		member.softDelete();
	}
}