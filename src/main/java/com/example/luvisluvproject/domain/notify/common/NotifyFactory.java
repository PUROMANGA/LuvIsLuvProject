package com.example.luvisluvproject.domain.notify.common;

import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.notify.NotifyCategory;
import com.example.luvisluvproject.domain.notify.dto.NotifyDto;
import com.example.luvisluvproject.domain.notify.entity.Notify;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotifyFactory {

	private final String ADMIN_EMAIL = "admin@luv.com";
	private final MemberRepository memberRepository;

	public Notify adminToMemberNotify(NotifyDto notifyDto, Member receiver) {
		Member admin = memberRepository.findByEmail(ADMIN_EMAIL)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		return new Notify(notifyDto, receiver, admin);
	}

	public NotifyDto adminToMemberNotifyDto(Member receiverMember, NotifyCategory content, boolean isRead) {
		Member admin = memberRepository.findByEmail(ADMIN_EMAIL)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		return new NotifyDto(admin, receiverMember, content, isRead);
	}
}
