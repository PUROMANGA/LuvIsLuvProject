package com.example.luvisluvproject.domain.report.service;

import com.example.luvisluvproject.domain.block.dto.BlockRequestDto;
import com.example.luvisluvproject.domain.block.service.BlockService;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.report.dto.ReportRequestDto;
import com.example.luvisluvproject.domain.report.dto.ReportResponseDto;
import com.example.luvisluvproject.domain.report.entity.Report;
import com.example.luvisluvproject.domain.report.repository.ReportRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;


import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ReportService
 * 신고 처리의 비즈니스 로직을 담당하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

	private final ReportRepository reportRepository;
	private final MemberRepository memberRepository;
	private final BlockService blockService;

	/**
	 * 신고 요청 처리
	 *
	 * @param reporterId 신고를 수행한 사용자 ID
	 * @param dto        신고 요청 정보
	 * @return ReportResponseDto 응답
	 */
	@Transactional
	public ReportResponseDto report(String email, ReportRequestDto dto) {

		//대상
		Member reported = memberRepository.findById(dto.getTargetId())
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		// 2. 신고자(Member) 조회
		Member reporter = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		Report report = new Report(reporter, reported, dto.getReason(), dto.getDescription());

		Report savedReport = reportRepository.save(report);

		// 5. 신고 완료 응답 반환
		return new ReportResponseDto(savedReport);
	}
}
