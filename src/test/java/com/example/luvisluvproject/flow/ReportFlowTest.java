package com.example.luvisluvproject.flow;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.luvisluvproject.domain.report.dto.ReportRequestDto;
import com.example.luvisluvproject.domain.report.dto.ReportResponseDto;
import com.example.luvisluvproject.domain.report.entity.ReportReason;
import com.example.luvisluvproject.domain.report.service.ReportService;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class ReportFlowTest {

	@Autowired
	private ReportService reportService;

	@Test
	@DisplayName("신고 요청 처리 테스트")
	void 사용자_정보_조회() {
		String meEmail = "test1234@email.com";
		ReportRequestDto reportRequestDto = new ReportRequestDto(
			3L,
			ReportReason.OTHER,
			"설명 예시"
		);
		ReportResponseDto reportResponseDto = reportService.report(meEmail, reportRequestDto);
		System.out.println("reportResponseDto = " + reportResponseDto);
		ApiResponse<ReportResponseDto> apiResponse = ApiResponse.of(SuccessCode.CREATE_REPORT_SUCCESS, reportResponseDto);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("신고가 성공적으로 접수되었습니다.");
	}
}
