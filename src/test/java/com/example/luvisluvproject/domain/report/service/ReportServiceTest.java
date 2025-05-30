package com.example.luvisluvproject.domain.report.service;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.report.dto.ReportRequestDto;
import com.example.luvisluvproject.domain.report.dto.ReportResponseDto;
import com.example.luvisluvproject.domain.report.entity.Report;
import com.example.luvisluvproject.domain.report.entity.ReportReason;
import com.example.luvisluvproject.domain.report.repository.ReportRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

	@Mock
	private ReportRepository reportRepository;

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private ReportService reportService;

	private Member reporter;
	private Member reported;

	@BeforeEach
	void setUp() {
		reporter = Member.builder().id(1L).name("reporter").build();
		reported = Member.builder().id(2L).name("reported").build();
	}

	@Test
	void 신고_성공() {
		// given
		ReportRequestDto requestDto = new ReportRequestDto(
			reported.getId(),
			ReportReason.INAPPROPRIATE_BEHAVIOR,
			"욕설을 하였습니다."
		);

		given(reportRepository.existsByReporterIdAndReportedId(reporter.getId(), reported.getId()))
			.willReturn(false);
		given(memberRepository.findById(reporter.getId()))
			.willReturn(Optional.of(reporter));
		given(memberRepository.findById(reported.getId()))
			.willReturn(Optional.of(reported));

		// when
		ReportResponseDto response = reportService.reportUser(reporter.getId(), requestDto);

		// then
		assertThat(response.getMessage()).isEqualTo("신고가 정상적으로 접수되었습니다.");
		then(reportRepository).should().save(any(Report.class));
	}

	@Test
	void 이미_신고한_사용자일_경우_예외발생() {
		// given
		ReportRequestDto requestDto = new ReportRequestDto(
			reported.getId(),
			ReportReason.SPAM,
			"지속적인 광고성 메시지"
		);

		given(reportRepository.existsByReporterIdAndReportedId(reporter.getId(), reported.getId()))
			.willReturn(true);

		// when & then
		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class,
			() -> reportService.reportUser(reporter.getId(), requestDto));

		assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.ALREADY_REPORTED);
	}

	@Test
	void 신고자_또는_피신고자_없을_경우_예외발생() {
		// given
		ReportRequestDto requestDto = new ReportRequestDto(
			reported.getId(),
			ReportReason.INAPPROPRIATE_BEHAVIOR,
			"비속어 사용"
		);

		given(reportRepository.existsByReporterIdAndReportedId(reporter.getId(), reported.getId()))
			.willReturn(false);
		given(memberRepository.findById(reporter.getId()))
			.willReturn(Optional.empty());

		// when & then
		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class,
			() -> reportService.reportUser(reporter.getId(), requestDto));

		assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.USER_CANT_FIND);
	}
}
