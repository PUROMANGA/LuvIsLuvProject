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
	 * 회원이 존재하지 않거나 소프트 삭제된 경우 예외를 발생시킵니다.
	 * @param memberId 조회할 회원의 ID
	 * @return 조회된 회원 정보를 담은 {@link MemberFindResponse}
	 * @throws CustomRuntimeException 회원이 존재하지 않거나 삭제된 경우 (ExceptionCode.MEMBER_NOT_FOUND)
	 */
	@Transactional(readOnly = true)
	public MemberFindResponse findById(Long memberId) {

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));

		if (member.isStatus()) {
			throw new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND);
		}

		return new MemberFindResponse(
			member.getId(),
			member.getName(),
			member.getEmail(),
			member.getBirthday()
		);
	}

	/**
	 * 주어진 회원 ID의 회원 비밀번호를 변경합니다.
	 * 이전 비밀번호가 일치하지 않거나 새 비밀번호가 이전 비밀번호와 같으면 예외를 발생시킵니다.
	 * 회원이 존재하지 않거나 소프트 삭제된 경우도 예외가 발생합니다.
	 * @param memberId 수정할 회원의 ID
	 * @param request 비밀번호 변경 요청 정보({@link MemberUpdateRequest})
	 * @throws CustomRuntimeException 회원이 존재하지 않거나 삭제된 경우 (ExceptionCode.MEMBER_NOT_FOUND)
	 * @throws CustomRuntimeException 이전 비밀번호가 일치하지 않는 경우 (ExceptionCode.PASSWORD_MISMATCH)
	 * @throws CustomRuntimeException 이전 비밀번호와 새 비밀번호가 같은 경우 (ExceptionCode.SAME_PASSWORD)
	 */
	@Transactional
	public void updateMember(Long memberId, MemberUpdateRequest request) {

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));

		if (member.isStatus()) {
			throw new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND);
		}

		if (!passwordEncoder.matches(request.getOldPassword(), member.getPassword())) {
			throw new CustomRuntimeException(ExceptionCode.PASSWORD_MISMATCH);
		}
		if (request.getOldPassword().equals(request.getNewPassword())) {
			throw new CustomRuntimeException(ExceptionCode.SAME_PASSWORD);
		}

		member.update(passwordEncoder.encode(request.getNewPassword()));
	}

	/**
	 * 주어진 회원 ID의 회원을 소프트 삭제 처리합니다.
	 * 비밀번호가 일치하지 않으면 예외를 발생시킵니다.
	 * 회원이 존재하지 않거나 소프트 삭제된 경우도 예외가 발생합니다.
	 * @param memberId 삭제할 회원의 ID
	 * @param request 삭제 요청 정보({@link MemberDeleteRequest})
	 * @throws CustomRuntimeException 회원이 존재하지 않거나 삭제된 경우 (ExceptionCode.MEMBER_NOT_FOUND)
	 * @throws CustomRuntimeException 비밀번호가 일치하지 않는 경우 (ExceptionCode.PASSWORD_MISMATCH)
	 */
	@Transactional
	public void deleteMember(Long memberId, @Valid MemberDeleteRequest request) {

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));

		if (member.isStatus()) {
			throw new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND);
		}

		if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
			throw new CustomRuntimeException(ExceptionCode.PASSWORD_MISMATCH);
		}
		member.softDelete();
	}
}
