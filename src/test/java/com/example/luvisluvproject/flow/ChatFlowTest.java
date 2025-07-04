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

import com.example.luvisluvproject.domain.chat.dto.ResponseChatRoom;
import com.example.luvisluvproject.domain.chat.dto.ResponseMessageDto;
import com.example.luvisluvproject.domain.chat.service.ChatService;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j

public class ChatFlowTest {

	@Autowired
	private ChatService chatService;

	@Test
	@DisplayName("채팅방 다 들고오기")
	void 사용자_정보_조회() {
		String meEmail = "test1234@email.com";
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "creatTime"));
		Slice<ResponseChatRoom> responseChatRooms = chatService.getAllChatRoomService(meEmail, pageable);
		System.out.println("responseChatRooms = " + responseChatRooms);
		ApiResponse<Slice<ResponseChatRoom>> apiResponse = ApiResponse.of(SuccessCode.SUCCESS_OK, responseChatRooms);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("요청이 정상적으로 처리되었습니다.");
	}

	@Test
	@DisplayName("채팅방 메세지 들고오기")
	void 채팅방_메세지_들고오기() {
		String meEmail = "test1234@email.com";
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "creatTime"));
		Slice<ResponseMessageDto> responseMessageDtos = chatService.getAndCheckMessage(meEmail, 1L, pageable);
		System.out.println("responseMessageDtos = " + responseMessageDtos);
		ApiResponse<Slice<ResponseMessageDto>> apiResponse = ApiResponse.of(SuccessCode.SUCCESS_OK, responseMessageDtos);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("요청이 정상적으로 처리되었습니다.");
	}

	@Test
	@DisplayName("채팅방 삭제 테스트")
	void 채팅방_삭제_테스트() {
		String meEmail = "test1234@email.com";
		chatService.deleteChatRoomService(meEmail, 1L);
		ApiResponse<Slice<ResponseMessageDto>> apiResponse = ApiResponse.of(SuccessCode.SUCCESS_OK, null);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("요청이 정상적으로 처리되었습니다.");
	}
}
