package com.example.luvisluvproject.domain.chat.controller;

import static org.springframework.data.domain.Sort.Direction.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.domain.chat.dto.RequestMessageDto;
import com.example.luvisluvproject.domain.chat.dto.ResponseMessageDto;
import com.example.luvisluvproject.domain.chat.service.ChatService;
import com.example.luvisluvproject.domain.member.entity.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor

public class ChatController {

	private final ChatService chatService;

	/**
	 * 메세지를 보내면 chatId로 올라오게 되고, 같은 chatId를 가진 사람에게도 보여지게 된다. 그 후 메세지 객체를 만들어 메세지를 저장
	 * @param requestMessageDto
	 * @param chatId
	 * @param member
	 */
	@MessageMapping("/chats/{chatId}")
	public void sendMessage(
		@Validated RequestMessageDto requestMessageDto,
		@DestinationVariable Long chatId,
		@AuthenticationPrincipal Member member) {
		chatService.sendChatMessage(requestMessageDto, chatId, member.getEmail());
	}

	@GetMapping("/chats/{chatId}")
	public ResponseEntity<Slice<ResponseMessageDto>> checkMessage(
		@AuthenticationPrincipal Member member,
		@PathVariable Long chatId,
		@PageableDefault(size = 10, sort = "creatTime", direction = DESC) Pageable pageable) {
		return ResponseEntity.ok(chatService.getAndCheckMessage(member.getEmail(), chatId, pageable));
	}
}
