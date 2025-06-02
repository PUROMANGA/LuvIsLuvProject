package com.example.luvisluvproject.domain.report.service;

import com.example.luvisluvproject.domain.block.dto.BlockRequestDto;
import com.example.luvisluvproject.domain.block.service.BlockService;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.report.dto.ReportRequestDto;
import com.example.luvisluvproject.domain.report.dto.ReportResponseDto;
import com.example.luvisluvproject.domain.report.entity.Report;
import com.example.luvisluvproject.domain.report.entity.ReportTargetType;
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
	private final BlockService blockService;

	@Transactional
	public ReportResponseDto report(Long reporterId, ReportRequestDto dto) {
		if (reportRepository.existsByReporterIdAndTargetIdAndTargetType(reporterId, dto.getTargetId(),
			dto.getTargetType())) {
			throw new CustomRuntimeException(ExceptionCode.ALREADY_REPORTED);
		}

		Member reporter = memberRepository.findById(reporterId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		Report report = Report.builder()
			.reporter(reporter)
			.targetType(dto.getTargetType())
			.targetId(dto.getTargetId())
			.reason(dto.getReason())
			.description(dto.getDescription())
			.build();

		Report savedReport = reportRepository.save(report);

		// ✅ USER만 자동 차단
		if (dto.getTargetType() == ReportTargetType.USER) {
			BlockRequestDto blockRequestDto = new BlockRequestDto(
				dto.getTargetId(),
				true,
				true,
				true,
				"AFTER_REPORT"
			);
			blockService.blockUser(reporterId, blockRequestDto);
		}

		return new ReportResponseDto("신고가 정상적으로 접수되었습니다.", savedReport.getCreatedAt());
	}
}
