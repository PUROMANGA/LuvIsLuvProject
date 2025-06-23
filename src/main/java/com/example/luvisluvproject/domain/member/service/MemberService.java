package com.example.luvisluvproject.domain.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.block.repository.BlockRepository;
import com.example.luvisluvproject.domain.block.service.BlockService;
import com.example.luvisluvproject.domain.member.dto.MemberDeleteRequest;
import com.example.luvisluvproject.domain.member.dto.MemberFindResponse;
import com.example.luvisluvproject.domain.member.dto.MemberMyProfileResponse;
import com.example.luvisluvproject.domain.member.dto.MemberUpdateProfile;
import com.example.luvisluvproject.domain.member.dto.MemberPasswordRequest;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.tag.repository.MemberTagRepository;
import com.example.luvisluvproject.domain.tag.repository.TagJpaRepository;
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
	private final TagJpaRepository tagJpaRepository;
	private final MemberTagRepository memberTagRepository;
	private final BlockService blockService;
	private final BlockRepository blockRepository;

	/**
	 * 회원 자신의 프로필 정보를 조회합니다.
	 * 회원이 존재하지 않거나 탈퇴 상태인 경우 예외가 발생합니다.
	 *
	 * @param memberId 조회할 회원의 ID
	 * @return 회원의 프로필 정보를 담은 {@link MemberMyProfileResponse}
	 * @throws CustomRuntimeException 회원이 존재하지 않거나 탈퇴한 경우
	 */
	@Transactional(readOnly = true)
	public MemberMyProfileResponse getMyProfile(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));

		if (member.isStatus()) {
			throw new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND);
		}

		return new MemberMyProfileResponse(member);
	}

	/**
	 * 다른 회원의 프로필 정보를 조회합니다.
	 * 대상 회원이 존재하지 않거나 탈퇴 상태일 경우,
	 * 혹은 차단된 회원일 경우 예외가 발생합니다.
	 *
	 */
	@Transactional(readOnly = true)
	public MemberFindResponse getMemberProfile(String email, Long memberId) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));

		Member targetMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));

		if (targetMember.isStatus()) {
			throw new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND);
		}

		if (blockRepository.existsByBlockerAndBlocked(member, targetMember)) {
			throw new CustomRuntimeException(ExceptionCode.PROFILE_ACCESS_DENIED);
		}

		return new MemberFindResponse(targetMember);
	}

	/**
	 * 주어진 회원 ID의 회원 비밀번호를 변경합니다.
	 * 이전 비밀번호가 일치하지 않거나 새 비밀번호가 이전 비밀번호와 같으면 예외를 발생시킵니다.
	 * 회원이 존재하지 않거나 소프트 삭제된 경우도 예외가 발생합니다.
	 */
	@Transactional
	public void checkPasswordMember(String email, MemberPasswordRequest request) {

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));

		if (member.isStatus()) {
			throw new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND);
		}

		if (!passwordEncoder.matches(request.getOldPassword(), member.getPassword())) {
			throw new CustomRuntimeException(ExceptionCode.PASSWORD_MISMATCH);
		}
	}

	/**
	 * 회원의 소개글(Content)을 수정합니다.
	 * 회원이 존재하지 않거나 탈퇴 상태인 경우 예외를 발생시키며,
	 * 현재 소개글과 동일한 내용으로 수정하려 할 경우에도 예외를 발생시킵니다.
	 */
	@Transactional
	public void updateMemberProfile(String email, MemberUpdateProfile request) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));

		if (member.isStatus()) {
			throw new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND);
		}

		String newPassword = passwordEncoder.encode(request.getNewPassword(););
		member.updateProfile(newPassword, request.getContent());
		memberRepository.save(member);
	}

	/**
	 * 주어진 회원 ID의 회원을 소프트 삭제 처리합니다.
	 * 비밀번호가 일치하지 않으면 예외를 발생시킵니다.
	 * 회원이 존재하지 않거나 소프트 삭제된 경우도 예외가 발생합니다.
	 */
	@Transactional
	public void deleteMember(String email, MemberDeleteRequest request) {

		Member member = memberRepository.findByEmail(email)
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
