package com.example.luvisluvproject.domain.report.service;

import com.example.luvisluvproject.domain.block.dto.BlockRequestDto;
import com.example.luvisluvproject.domain.block.service.BlockService;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.report.dto.ReportRequestDto;
import com.example.luvisluvproject.domain.report.dto.ReportResponseDto;
import com.example.luvisluvproject.domain.report.entity.Report;
import com.example.luvisluvproject.domain.report.entity.ReportReason;
import com.example.luvisluvproject.domain.report.entity.ReportTargetType;
import com.example.luvisluvproject.domain.report.repository.ReportRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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

	@Mock
	private BlockService blockService;

	@InjectMocks
	private ReportService reportService;

	private Member reporter;
	private Member targetUser;

	@BeforeEach
	void setUp() {
		reporter = Member.builder().id(1L).name("reporter").email("reporter@email.com").build();
		targetUser = Member.builder().id(2L).name("targetUser").email("target@email.com").build();
	}

	@Test
	void 유저신고_성공시_자동차단되고_신고횟수증가_및_저장된다() {
		// given
		ReportRequestDto requestDto = new ReportRequestDto(
			ReportTargetType.USER,
			targetUser.getId(),
			ReportReason.SPAM,
			"욕설 반복"
		);

		given(memberRepository.findByEmail(reporter.getEmail())).willReturn(Optional.of(reporter));
		given(memberRepository.findById(targetUser.getId())).willReturn(Optional.of(targetUser));
		given(reportRepository.existsByReporterEmailAndTargetIdAndTargetType(
			reporter.getEmail(), targetUser.getId(), ReportTargetType.USER
		)).willReturn(false);
		given(reportRepository.save(any(Report.class)))
			.willReturn(Report.builder()
				.reporter(reporter)
				.targetType(ReportTargetType.USER)
				.targetId(targetUser.getId())
				.createdAt(LocalDateTime.now())
				.build());

		// when
		ReportResponseDto response = reportService.report(reporter.getEmail(), requestDto);

		// then
		assertThat(response).isNotNull();
		then(blockService).should().blockUser(eq(reporter.getEmail()), any(BlockRequestDto.class));
		then(memberRepository).should().save(eq(targetUser));
	}

	@Test
	void 메시지신고_성공시_자동차단되지_않는다() {
		// given
		Long messageId = 99L;
		ReportRequestDto requestDto = new ReportRequestDto(
			ReportTargetType.MESSAGE,
			messageId,
			ReportReason.SEXUAL_CONTENT,
			"부적절한 이미지"
		);

		given(memberRepository.findByEmail(reporter.getEmail())).willReturn(Optional.of(reporter));
		given(reportRepository.existsByReporterEmailAndTargetIdAndTargetType(
			reporter.getEmail(), messageId, ReportTargetType.MESSAGE
		)).willReturn(false);
		given(reportRepository.save(any(Report.class)))
			.willReturn(Report.builder()
				.reporter(reporter)
				.targetType(ReportTargetType.MESSAGE)
				.targetId(messageId)
				.createdAt(LocalDateTime.now())
				.build());

		// when
		ReportResponseDto response = reportService.report(reporter.getEmail(), requestDto);

		// then
		assertThat(response).isNotNull();
		then(blockService).shouldHaveNoInteractions();
	}

	@Test
	void 이미_신고한_경우_예외_발생() {
		// given
		ReportRequestDto requestDto = new ReportRequestDto(
			ReportTargetType.USER,
			targetUser.getId(),
			ReportReason.SPAM,
			"이미 신고함"
		);

		given(reportRepository.existsByReporterEmailAndTargetIdAndTargetType(
			reporter.getEmail(), targetUser.getId(), ReportTargetType.USER
		)).willReturn(true);

		// when & then
		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class,
			() -> reportService.report(reporter.getEmail(), requestDto));

		assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.ALREADY_REPORTED);
	}

	@Test
	void 신고자_없는_경우_예외_발생() {
		// given
		ReportRequestDto requestDto = new ReportRequestDto(
			ReportTargetType.USER,
			targetUser.getId(),
			ReportReason.ABUSIVE_LANGUAGE,
			"없는 신고자"
		);

		given(reportRepository.existsByReporterEmailAndTargetIdAndTargetType(
			reporter.getEmail(), targetUser.getId(), ReportTargetType.USER
		)).willReturn(false);
		given(memberRepository.findByEmail(reporter.getEmail())).willReturn(Optional.empty());

		// when & then
		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class,
			() -> reportService.report(reporter.getEmail(), requestDto));

		assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.USER_CANT_FIND);
	}
}
