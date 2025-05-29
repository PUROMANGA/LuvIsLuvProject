package com.example.luvisluvproject.domain.report.service;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.report.dto.ReportRequestDto;
import com.example.luvisluvproject.domain.report.dto.ReportResponseDto;
import com.example.luvisluvproject.domain.report.entity.Report;
import com.example.luvisluvproject.domain.report.repository.ReportRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

	private final ReportRepository reportRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public ReportResponseDto reportUser(Long reporterId, ReportRequestDto dto) {
		if (reportRepository.existsByReporterIdAndReportedId(reporterId, dto.getReportedId())) {
			throw new CustomRuntimeException(ExceptionCode.ALREADY_REPORTED);
		}

		Member reporter = memberRepository.findById(reporterId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		Member reported = memberRepository.findById(dto.getReportedId())
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		Report report = Report.builder()
			.reporter(reporter)
			.reported(reported)
			.reason(dto.getReason())
			.description(dto.getDescription())
			.build();

		reportRepository.save(report);
		return new ReportResponseDto("신고가 정상적으로 접수되었습니다.");
	}
}