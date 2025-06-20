package com.example.luvisluvproject.domain.report.service;

import com.example.luvisluvproject.domain.block.dto.BlockRequestDto;
import com.example.luvisluvproject.domain.block.enums.BlockType;
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
	 * @param reporterEmail 로그인한 사용자의 이메일
	 * @param dto           신고 요청 정보
	 * @return ReportResponseDto 응답 DTO
	 */
	@Transactional
	public ReportResponseDto report(String reporterEmail, ReportRequestDto dto) {

		// 1. 중복 신고 여부 확인 (이메일 기반)
		if (reportRepository.existsByReporterEmailAndTargetEmailAndTargetType(
			reporterEmail, dto.getTargetEmail(), dto.getTargetType())) {
			throw new CustomRuntimeException(ExceptionCode.ALREADY_REPORTED);
		}

		// 2. 신고자 조회 (이메일은 로그인된 유저 정보)
		Member reporter = memberRepository.findByEmail(reporterEmail)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		// 2. 신고 대상 조회
		Member target = memberRepository.findByEmail(dto.getTargetEmail())
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		// 3. 신고 객체 생성 및 저장
		Report report = Report.builder()
			.reporter(reporter)
			.targetType(dto.getTargetType())
			.targetId(target.getId())
			.targetEmail(dto.getTargetEmail())
			.reason(dto.getReason())
			.description(dto.getDescription())
			.build();


		Report savedReport = reportRepository.save(report);

		// 4. 신고 대상이 사용자일 경우
		if (dto.getTargetType() == ReportTargetType.USER) {

			Member reportedUser = memberRepository.findByEmail(dto.getTargetEmail())
				.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

			BlockRequestDto blockRequestDto = new BlockRequestDto(
				dto.getTargetEmail(),
				true,
				true,
				BlockType.AFTER_REPORT
			);
			blockService.blockUser(reporterEmail, blockRequestDto);

			// 4-3. 신고 누적 횟수 증가
			reportedUser.increaseReportCount();
			memberRepository.save(reportedUser);
		}

		// 5. 신고 응답
		return new ReportResponseDto(savedReport.getCreatedAt());
	}
}