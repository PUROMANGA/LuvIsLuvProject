package com.example.luvisluvproject.admin.sanction.service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.admin.sanction.common.SanctionHandler;
import com.example.luvisluvproject.admin.sanction.dto.SanctionReportDto;
import com.example.luvisluvproject.admin.sanction.dto.SanctionRequestDto;
import com.example.luvisluvproject.admin.sanction.dto.SanctionSuccessDto;
import com.example.luvisluvproject.admin.sanction.entity.Sanction;
import com.example.luvisluvproject.admin.sanction.repository.SanctionRepository;
import com.example.luvisluvproject.domain.member.dto.MemberInformationDto;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.report.repository.ReportRepository;

@Service
public class SanctionService {

	private final MemberRepository memberRepository;
	private final ReportRepository reportRepository;
	private final SanctionRepository sanctionRepository;
	private final RedisTemplate<String, String> stringStringRedisTemplate;
	private final SanctionHandler sanctionHandler;

	public SanctionService(MemberRepository memberRepository, ReportRepository reportRepository,
		SanctionRepository sanctionRepository, @Qualifier("customStringRedisTemplate") RedisTemplate<String, String> stringStringRedisTemplate,
		SanctionHandler sanctionHandler) {
		this.memberRepository = memberRepository;
		this.reportRepository = reportRepository;
		this.sanctionRepository = sanctionRepository;
		this.stringStringRedisTemplate = stringStringRedisTemplate;
		this.sanctionHandler = sanctionHandler;
	}

	@Transactional(readOnly = true)
	public Page<MemberInformationDto> getMembersSanctionsService(Pageable pageable) {
		return memberRepository.findAllReportedInactiveMembers(pageable).map(MemberInformationDto::new);
	}

	@Transactional(readOnly = true)
	public Page<SanctionReportDto> getSanctionReportService(Long memberId, Pageable pageable) {
		return reportRepository.findMemberInformationDtoByMemberId(memberId, pageable);
	}

	@Transactional
	public SanctionSuccessDto createSanctionService(Long memberId, SanctionRequestDto sanctionRequestDto) {
		Sanction sanction = new Sanction(memberId, true, sanctionRequestDto);
		sanctionRepository.save(sanction);
		Duration duration = sanctionHandler.makeDuration(sanction.getBanExpireAt());
		stringStringRedisTemplate.opsForValue().set("ban:" + memberId, "true", duration.getSeconds(), TimeUnit.SECONDS);
		return new SanctionSuccessDto(sanction);
	}
}
