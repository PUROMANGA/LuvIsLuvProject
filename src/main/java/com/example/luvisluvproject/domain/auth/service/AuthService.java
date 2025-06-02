package com.example.luvisluvproject.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.auth.dto.request.SignupRequestDto;
import com.example.luvisluvproject.domain.auth.dto.response.SignupResponseDto;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.enums.UserRole;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public SignupResponseDto signup(SignupRequestDto requestDto) {

		// 이메일 중복확인
		if (memberRepository.existsByEmail(requestDto.getEmail())) {
			throw new CustomRuntimeException(ExceptionCode.EMAIL_ALREADY_EXIST);
		}

		if (memberRepository.existsByName(requestDto.getName())) {
			throw new CustomRuntimeException(ExceptionCode.NAME_ALREADY_EXIST);
		}

		// 비밀번호 암호화
		String encodePassword = passwordEncoder.encode(requestDto.getPassword());

		// userRole string -> enum 변환
		UserRole userRole = UserRole.of(requestDto.getUserRole());

		Member member = new Member(
			requestDto.getName(),
			requestDto.getEmail(),
			encodePassword,
			requestDto.getBirthday(),
			userRole
		);

		Member saved = memberRepository.save(member);

		return new SignupResponseDto(
			saved.getId(),
			saved.getName(),
			saved.getEmail(),
			saved.getBirthday(),
			saved.getUserRole()
		);
	}

}
