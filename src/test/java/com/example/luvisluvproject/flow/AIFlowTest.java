package com.example.luvisluvproject.flow;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import com.example.luvisluvproject.domain.ai.service.OpenAiService;
import com.example.luvisluvproject.domain.memberInteractionLog.dto.MemberInteractionLogDto;
import com.example.luvisluvproject.domain.notify.dto.NotifyDto;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import groovy.util.logging.Slf4j;

@SpringBootTest
@Slf4j

public class AIFlowTest {

	@Autowired
	private OpenAiService openAiService;

	@Test
	@DisplayName("ai 제출 어제 테스트")
	void 프롬프트_테스트1() {

		LocalDate yesterDay = LocalDate.now().minusDays(1);
		String meEmail = "test1234@email.com";
		MemberInteractionLogDto memberInteractionLogDto = openAiService.getMemberInteractionLogDto(meEmail, yesterDay);
		System.out.println("memberInteractionLogDto = " + memberInteractionLogDto);
		ApiResponse<MemberInteractionLogDto> apiResponse = ApiResponse.of(SuccessCode.SUCCESS_OK, memberInteractionLogDto);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("요청이 정상적으로 처리되었습니다.");
	}

	@Test
	@DisplayName("ai 제출 오늘 테스트")
	void 프롬프트_테스트2() {

		LocalDate now = LocalDate.now();
		String meEmail = "test1234@email.com";
		MemberInteractionLogDto memberInteractionLogDto = openAiService.getMemberInteractionLogDto(meEmail, now);
		System.out.println("memberInteractionLogDto = " + memberInteractionLogDto);
		ApiResponse<MemberInteractionLogDto> apiResponse = ApiResponse.of(SuccessCode.SUCCESS_OK, memberInteractionLogDto);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("요청이 정상적으로 처리되었습니다.");
	}
}
