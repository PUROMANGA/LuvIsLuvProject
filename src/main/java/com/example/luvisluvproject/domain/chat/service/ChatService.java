package com.example.luvisluvproject.domain.chat.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.chat.dto.RequestMessageDto;
import com.example.luvisluvproject.domain.chat.dto.ResponseMessageDto;
import com.example.luvisluvproject.domain.chat.entity.ChatRoom;
import com.example.luvisluvproject.domain.chat.entity.Message;
import com.example.luvisluvproject.domain.chat.repository.ChatRoomRepository;
import com.example.luvisluvproject.domain.chat.repository.MessageRepository;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class ChatService {

	private final SimpMessagingTemplate simpMessagingTemplate;
	private final ChatRoomRepository chatRoomRepository;
	private final MemberRepository memberRepository;
	private final MessageRepository messageRepository;

	/**
	 * /pub/chats/chatID로 메세지가 보내지면 /sub/chats/chatId로 convertAndSend를 사용해서 메세지를 전달하게 됩니다.
	 * @param destination
	 * @return
	 */
	@Transactional
	public void sendChatMessage(RequestMessageDto requestMessageDto, Long chatId, String email) {
		Boolean isRead = false;
		simpMessagingTemplate.convertAndSend("/sub/chats/" + chatId, requestMessageDto);
		ChatRoom chatRoom = chatRoomRepository.findById(chatId).orElseThrow(() -> new RuntimeException("채팅방이 없습니다."));
		Member me = memberRepository.findByEmail(email).orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		Member opponent = chatRoom.checkMember(me);
		Message message = new Message(chatRoom, me, opponent, isRead, requestMessageDto);
		messageRepository.save(message);
	}

	/**
	 * 채팅방에 들어있는 메세지를 전부 들고오고, 내가 아닌 사람이 보낸 메세지를 읽음으로 표시합니다.
	 * @param email
	 * @param chatId
	 * @param pageable
	 * @return
	 */
	@Transactional
	public Slice<ResponseMessageDto> getAndCheckMessage(String email, Long chatId, Pageable pageable) {
		Slice<Message> messageList = messageRepository.findAllByChatId(chatId, pageable);

		List<ResponseMessageDto> responseMessageDtoList = messageList.stream()
			.peek(m -> m.updateIsRead())
			.map(ResponseMessageDto::new)
			.toList();

		return new SliceImpl<>(responseMessageDtoList, pageable, messageList.hasNext());
	}

	/**
	 * 세션에 방 아이디를 저장해야하기 때문에 목적지인 주소를 subString해주고 roomId를 찾아서 return해줍니다.
	 * @param destination
	 * @return
	 */
	public Long submitRoomId(String destination) {
		if (destination == null) {
			throw new RuntimeException("잘못된 방 주소 입니다");
		}
		String roomId = destination.substring(destination.lastIndexOf("/") + 1);
		return Long.parseLong(roomId);
	}

	/**
	 * 구독이 시작됐을 때
	 * @param message
	 */
	public void sendChatEnterMessage(Message message) {
		String destination = "sub/chats/" + message.getChatRoom().getId();
		simpMessagingTemplate.convertAndSend(destination, message);
	}
}
