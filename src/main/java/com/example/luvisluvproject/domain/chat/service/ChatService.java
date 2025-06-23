package com.example.luvisluvproject.domain.chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.chat.dto.ResponseChatRoom;
import com.example.luvisluvproject.domain.chat.dto.ResponseChatRoomCount;
import com.example.luvisluvproject.domain.chat.dto.ResponseMessageDto;
import com.example.luvisluvproject.domain.chat.entity.ChatRoom;
import com.example.luvisluvproject.domain.chat.entity.MemberChatRoom;
import com.example.luvisluvproject.domain.chat.entity.Message;
import com.example.luvisluvproject.domain.chat.repository.ChatRoomRepository;
import com.example.luvisluvproject.domain.chat.repository.MemberChatRoomRepository;
import com.example.luvisluvproject.domain.chat.mongorepository.MessageRepository;
import com.example.luvisluvproject.domain.chat.dto.MessageDto;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.config.JwtUtil;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;
import com.example.luvisluvproject.global.redis.RedisPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class ChatService {

	private final SimpMessagingTemplate simpMessagingTemplate;
	private final ChatRoomRepository chatRoomRepository;
	private final MemberRepository memberRepository;
	private final MessageRepository messageRepository;
	private final MemberChatRoomRepository memberChatRoomRepository;
	private final RedisPublisher redisPublisher;
	private final Map<String, String> stompToWebSocketMap;
	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * chats/message에 프론트에서 입력된 메세지를 발행
	 * @param messageDto
	 */
	@Transactional
	public void sendMessage(MessageDto messageDto, String email) {
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		messageDto.setUserId(me.getId());
		messageDto.setSenderName(me.getName());
		Message message = new Message(messageDto);
		Message saved = messageRepository.save(message);

		String webSocketSessionId = stompToWebSocketMap.get(email);
		if(redisTemplate.opsForSet().members(webSocketSessionId) == null) {
			messageRepository.save(saved);
		}

		System.out.println("✅ 저장된 메시지 ID: " + saved.getId());
		System.out.println("🕒 생성 시간: " + saved.getCreatTime());
		redisPublisher.publish(messageDto);
	}

	/**
	 * 해당 사용자가 들어가있는 채팅방을 전부 다 불러옵니다.
	 * @param email
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public Slice<ResponseChatRoom> getAllChatRoomService(String email, Pageable pageable) {
		//나를 찾고
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		Slice<ResponseChatRoom> memberChatRoomList = memberChatRoomRepository.findAllByMemberIdAndDeletedFalse(
				me.getId(), pageable)
			.map(memberChatRoom -> {
				Member findMe = memberRepository.findById(memberChatRoom.getMemberId())
					.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
				ChatRoom chatRoom = memberChatRoom.getChatRoom();
				Member opponent = chatRoom.checkMember(findMe);
				Message message = messageRepository.findFirstByChatRoomIdOrderByCreatTimeDesc(chatRoom.getId())
					.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MESSAGE_NOT_FOUNT));
				return new ResponseChatRoom(chatRoom.getId(), opponent.getName(), message.getContent());
			});

		return memberChatRoomList;
	}

	/**
	 * 채팅방에 들어있는 메세지를 전부 들고오기
	 * @param email
	 * @param chatId
	 * @param pageable
	 * @return
	 */
	@Transactional
	public Slice<ResponseMessageDto> getAndCheckMessage(String email, Long chatId, Pageable pageable) {
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		if (!memberChatRoomRepository.existsByMemberIdAndChatRoomId(me.getId(), chatId)) {
			throw new CustomRuntimeException(ExceptionCode.CHAT_ROOM_NOT_FOUND);
		}
		return messageRepository.findAllByChatRoomId(chatId, pageable)
			.map(ResponseMessageDto::new);
	}

	/**
	 * member 확인해주면서 채팅방 삭제
	 * @param email
	 * @param chatId
	 */
	@Transactional
	public void deleteChatRoomService(String email, Long chatId) {
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		ChatRoom chatRoom = chatRoomRepository.findById(chatId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.CHAT_ROOM_NOT_FOUND));
		MemberChatRoom memberChatRoom = memberChatRoomRepository.findByMemberIdAndChatRoomId(me.getId(), chatId);

		if (me.equals(chatRoom.getMemberA()) || me.equals(chatRoom.getMemberB())) {
			memberChatRoom.setDeleted(true);
			chatRoom.plusDeleteCount();
			if (chatRoom.getDeleteCount() == 2) {
				chatRoomRepository.delete(chatRoom);
				memberChatRoomRepository.delete(memberChatRoom);
			}
		}
	}

	// /**
	//  * 내가 메세지를 봤는데 내가 보낸 게 아니라면 읽음 표시를 합니다.
	//  * @param messageId
	//  * @param member
	//  */
	//
	// @Transactional
	// public void updateIsReadService(Long messageId, String email) {
	// 	Message message = messageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("메세지가 없습니다."));
	// 	Member me = memberRepository.findByEmail(email)
	// 		.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
	//
	// 	if (!message.getSenderId().equals(me.getId())) {
	// 		message.updateIsRead();
	// 		messageRepository.save(message);
	// 	}
	// }
}
