package com.example.luvisluvproject.domain.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.member.dto.MemberFindResponse;
import com.example.luvisluvproject.domain.member.dto.MemberUpdateRequest;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

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

	public MemberFindResponse findById(Long id) {

		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));
		return new MemberFindResponse(member.getId(),
			member.getName(),
			member.getEmail(),
			member.getBirthday()
		);
	}

	@Transactional
	public void updateMember(Long id, MemberUpdateRequest memberUpdateRequest) {

		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));

		if (!passwordEncoder.matches(memberUpdateRequest.getOldPassword(), member.getPassword())) {
			throw new CustomRuntimeException(ExceptionCode.PASSWORD_MISMATCH);
		}
		if (memberUpdateRequest.getOldPassword().equals(memberUpdateRequest.getNewPassword())) {
			throw new CustomRuntimeException(ExceptionCode.SAME_PASSWORD);
		}
		member.update(passwordEncoder.encode(memberUpdateRequest.getNewPassword()));
	}

}