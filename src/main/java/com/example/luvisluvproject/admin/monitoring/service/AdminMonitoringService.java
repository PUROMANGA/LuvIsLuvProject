package com.example.luvisluvproject.admin.monitoring.service;

import org.springframework.stereotype.Service;

import com.example.luvisluvproject.domain.memberInteractionLog.dto.MemberInteractionLogDto;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberInteractionLog;
import com.example.luvisluvproject.domain.memberInteractionLog.repository.MemberInteractionLogRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

@Service
public class AdminMonitoringService {

	private final MemberRepository memberRepository;
	private final MemberInteractionLogRepository memberInteractionLogRepository;

	public AdminMonitoringService(MemberRepository memberRepository,
		MemberInteractionLogRepository memberInteractionLogRepository) {
		this.memberRepository = memberRepository;
		this.memberInteractionLogRepository = memberInteractionLogRepository;
	}

	// public MemberInteractionLogDto getMemberInteractionLogService(Long memberId) {
	//
	// 	MemberInteractionLog memberInteractionLog = memberInteractionLogRepository.findById(memberId)
	// 		.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
	//
	// 	return new MemberInteractionLogDto(memberInteractionLog);
	// }
}
