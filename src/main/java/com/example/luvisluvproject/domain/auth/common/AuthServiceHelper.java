package com.example.luvisluvproject.domain.auth.common;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.enums.UserRole;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthServiceHelper {
	private final MemberRepository memberRepository;

	public Member createMember(String email, String name, LocalDate birthday, String encodePassword, UserRole userRole) {

		//이름 중복 확인
		if (memberRepository.existsByName(name)) {
			throw new CustomRuntimeException(ExceptionCode.NAME_ALREADY_EXIST);
		}

		//이메일 중복 확인
		if (memberRepository.existsByEmail(email)) {
			throw new CustomRuntimeException(ExceptionCode.EMAIL_ALREADY_EXIST);
		}

		// 오늘 날짜 이후로 생일 설정한 경우
		LocalDate today = LocalDate.now();
		if (birthday.isAfter(today)) {
			throw new CustomRuntimeException(ExceptionCode.INVALID_BIRTHDAY_IN_FUTURE);
		}

		// 미성년자 확인
		if (birthday.plusYears(19).isAfter(today)) {
			throw new CustomRuntimeException(ExceptionCode.UNDERAGE_USER);
		}

		if(userRole.equals(UserRole.USER)) {
			Member member = new Member(name, email, encodePassword, birthday, UserRole.USER);
			memberRepository.save(member);
			return member;
		} else if (userRole.equals(UserRole.MANAGER)) {
			Member member = new Member(name, email, encodePassword, birthday, UserRole.MANAGER);
			memberRepository.save(member);
			return member;
		} else {
			throw new RuntimeException("어드민 권한으로는 회원가입할 수 없습니다.");
		}
	}
}
