package com.example.luvisluvproject.domain.chat.controller;

import static org.springframework.data.domain.Sort.Direction.*;

import java.security.Principal;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.domain.chat.dto.ChatEnterRequest;
import com.example.luvisluvproject.domain.chat.dto.MessageDto;
import com.example.luvisluvproject.domain.chat.dto.ResponseChatRoom;
import com.example.luvisluvproject.domain.chat.dto.ResponseMessageDto;
import com.example.luvisluvproject.domain.chat.service.ChatService;
import com.example.luvisluvproject.global.common.AuthUser;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor

public class ChatController {

	private final ChatService chatService;

	/**
	 * chats/message에 프론트에서 입력된 메세지를 발행
	 * @param messageDto
	 */
	@MessageMapping("/chats/message")
	public void handleMessage(MessageDto messageDto,
		Principal principal) {
		// @Header("Authorization") String token) {
		System.out.println("📥 메시지 핸들러 진입! = " + messageDto);
		chatService.sendMessage(messageDto, principal.getName());
	}

	/**
	 * 해당 사용자가 들어가있는 채팅방을 전부 다 불러옵니다.
	 * @param member
	 * @param pageable
	 * @return
	 */
	@GetMapping("/chats")
	public ResponseEntity<ApiResponse<Slice<ResponseChatRoom>>> getAllChatRoom(
		@AuthenticationPrincipal AuthUser member,
		@PageableDefault(size = 10, sort = "creatTime", direction = DESC) Pageable pageable) {
		ApiResponse apiResponse = ApiResponse.of(SuccessCode.SUCCESS_OK,
			chatService.getAllChatRoomService(member.getUsername(), pageable));
		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * 채팅방에 들어있는 메세지를 전부 들고오기
	 * @param member
	 * @param chatId
	 * @param pageable
	 * @return
	 */

	@GetMapping("/chats/{chatId}")
	public ResponseEntity<ApiResponse<Slice<ResponseMessageDto>>> checkMessage(
		@AuthenticationPrincipal AuthUser member,
		@PathVariable Long chatId,
		@PageableDefault(size = 10, sort = "creatTime", direction = DESC) Pageable pageable) {
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.SUCCESS_OK,
			chatService.getAndCheckMessage(member.getUsername(), chatId, pageable)));
	}

	/**
	 * member 확인해주면서 채팅방 삭제
	 * @param member
	 * @param chatId
	 * @return
	 */

	@PatchMapping("/chats/{chatId}")
	public ResponseEntity<ApiResponse<Void>> deleteChatRoom(
		@AuthenticationPrincipal AuthUser member,
		@PathVariable Long chatId) {
		chatService.deleteChatRoomService(member.getUsername(), chatId);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.DELETE_OK));
	}

	/**
	 *  읽음 처리
	 */
	@MessageMapping("/chat/enter")
	public void enterRoom(ChatEnterRequest chatEnterRequest,
		Principal principal) {
		chatService.markMessagesAsRead(chatEnterRequest, principal.getName());
	}
}
