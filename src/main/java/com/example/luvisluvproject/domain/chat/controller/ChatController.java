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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
	 * /pub/chats/chatID로 메세지가 보내지면 /sub/chats/chatId로 convertAndSend를 사용해서 메세지를 전달하게 됩니다.
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

	/**
	 * 내가 메세지를 봤는데 내가 보낸 게 아니라면 읽음 표시를 합니다.
	 * @param messageId
	 * @param member
	 * @return
	 */

	@PatchMapping("/messages/{messageId}")
	public ResponseEntity<String> updateIsRead(
		@PathVariable Long messageId,
		@AuthenticationPrincipal Member member) {
		chatService.updateIsReadService(messageId, member);
		return ResponseEntity.ok("읽음 처리 완료");
	}

	/**
	 * 채팅방에 들어있는 메세지를 전부 들고오고, 내가 아닌 사람이 보낸 메세지를 읽음으로 표시합니다.
	 * @param member
	 * @param chatId
	 * @param pageable
	 * @return
	 */

	@GetMapping("/chats/{chatId}")
	public ResponseEntity<Slice<ResponseMessageDto>> checkMessage(
		@AuthenticationPrincipal Member member,
		@PathVariable Long chatId,
		@PageableDefault(size = 10, sort = "creatTime", direction = DESC) Pageable pageable) {
		return ResponseEntity.ok(chatService.getAndCheckMessage(member.getEmail(), chatId, pageable));
	}

	/**
	 * member 확인해주면서 채팅방 삭제
	 * @param member
	 * @param chatId
	 * @return
	 */

	@DeleteMapping("/chats/{chatId}")
	public ResponseEntity<String> deleteChatRoom(
		@AuthenticationPrincipal Member member,
		@PathVariable Long chatId) {
		chatService.deleteChatRoomService(member, chatId);
		return ResponseEntity.ok("삭제완료");
	}
}
