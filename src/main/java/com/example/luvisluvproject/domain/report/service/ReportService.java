package com.example.luvisluvproject.domain.report.service;

import com.example.luvisluvproject.domain.block.dto.BlockRequestDto;
import com.example.luvisluvproject.domain.block.dto.BlockRequestDtoFactory;
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
		// 1. 중복 신고 방지
		if (reportRepository.existsByReporterIdAndTargetIdAndTargetType(reporterId, dto.getTargetId(), dto.getTargetType())) {
			throw new CustomRuntimeException(ExceptionCode.ALREADY_REPORTED);
		}

		// 2. 신고자 조회
		Member reporter = memberRepository.findById(reporterId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		// 3. 신고 객체 생성 및 저장
		Report report = Report.builder()
			.reporter(reporter)
			.targetType(dto.getTargetType())
			.targetId(dto.getTargetId())
			.reason(dto.getReason())
			.description(dto.getDescription())
			.build();
		Report savedReport = reportRepository.save(report);

		// 4. 대상이 유저인 경우 추가 처리
		if (dto.getTargetType() == ReportTargetType.USER) {
			// 4-1. 신고 대상자 조회
			Member target = memberRepository.findById(dto.getTargetId())
				.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

			// 4-2. 자동 차단 처리
			BlockRequestDto blockRequestDto = BlockRequestDtoFactory.afterReportBlock(dto.getTargetId());
			blockService.blockUser(reporterId, blockRequestDto);

			// 4-3. 신고 누적 처리 및 활동 제한 적용
			target.increaseReportCount();
			memberRepository.save(target);
		}

		// 5. 응답 반환
		return new ReportResponseDto("신고가 정상적으로 접수되었습니다.", savedReport.getCreatedAt());
	}
}
