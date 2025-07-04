package com.example.luvisluvproject.flow;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import com.example.luvisluvproject.domain.notify.dto.NotifyDto;
import com.example.luvisluvproject.domain.notify.service.NotifyService;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import groovy.util.logging.Slf4j;

@SpringBootTest
@Slf4j

public class NotifyFlowTest {

	@Autowired
	private NotifyService notifyService;

	@Test
	@DisplayName("채팅방 다 들고오기")
	void 사용자_정보_조회() {
		String meEmail = "test1234@email.com";
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "creatTime"));
		Slice<NotifyDto> notifyDtos = notifyService.getNotifyService(meEmail, pageable);
		System.out.println("notifyDtos = " + notifyDtos);
		ApiResponse<Slice<NotifyDto>> apiResponse = ApiResponse.of(SuccessCode.SUCCESS_OK, notifyDtos);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("요청이 정상적으로 처리되었습니다.");
	}
}
