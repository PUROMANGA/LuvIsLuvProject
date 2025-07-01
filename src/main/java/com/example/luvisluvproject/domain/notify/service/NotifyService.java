package com.example.luvisluvproject.domain.notify.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.notify.dto.NotifyDto;
import com.example.luvisluvproject.domain.notify.event.NotifyLogEvent;
import com.example.luvisluvproject.domain.notify.repository.NotifyRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Service
public class NotifyService {

	private final NotifyRepository notifyRepository;
	private final MemberRepository memberRepository;

	public NotifyService(NotifyRepository notifyRepository, MemberRepository memberRepository) {
		this.notifyRepository = notifyRepository;
		this.memberRepository = memberRepository;
	}

	/**
	 * 내가 받았단 알람달을 전부 다 가져옵니다.
	 * @param email
	 * @param pageable
	 * @return
	 */
	@Transactional
	public Slice<NotifyDto> getNotifyService(String email, Pageable pageable) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));
		return notifyRepository.findByMemberId(member.getId(), pageable).map(notify -> new NotifyDto(notify, member.getId()));
	}
}
