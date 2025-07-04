package com.example.luvisluvproject.domain.chat.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.chat.common.ChatHandler;
import com.example.luvisluvproject.domain.chat.dto.ChatEnterRequest;
import com.example.luvisluvproject.domain.chat.dto.MessageDto;
import com.example.luvisluvproject.domain.chat.dto.ResponseChatRoom;
import com.example.luvisluvproject.domain.chat.dto.ResponseMessageDto;
import com.example.luvisluvproject.domain.chat.entity.ChatRoom;
import com.example.luvisluvproject.domain.chat.entity.MemberChatRoom;
import com.example.luvisluvproject.domain.chat.entity.Message;
import com.example.luvisluvproject.domain.chat.mongorepository.MessageRepository;
import com.example.luvisluvproject.domain.chat.repository.ChatRoomRepository;
import com.example.luvisluvproject.domain.chat.repository.MemberChatRoomRepository;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.notify.event.NotifyChatEvent;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;
import com.example.luvisluvproject.global.redis.RedisPublisher;

@Service

public class ChatService {

	private final SimpMessagingTemplate simpMessagingTemplate;
	private final ChatRoomRepository chatRoomRepository;
	private final MemberRepository memberRepository;
	private final MessageRepository messageRepository;
	private final MemberChatRoomRepository memberChatRoomRepository;
	private final RedisPublisher redisPublisher;
	private final RedisTemplate<String, String> customStringRedisTemplate;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final ChatHandler chatHandler;

	public ChatService(SimpMessagingTemplate simpMessagingTemplate, ChatRoomRepository chatRoomRepository,
		MemberRepository memberRepository, MessageRepository messageRepository,
		MemberChatRoomRepository memberChatRoomRepository, RedisPublisher redisPublisher,
		@Qualifier("customStringRedisTemplate") RedisTemplate<String, String> customStringRedisTemplate,
		RedisTemplate<String, Object> redisTemplate,
		ApplicationEventPublisher applicationEventPublisher, ChatHandler chatHandler) {
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.chatRoomRepository = chatRoomRepository;
		this.memberRepository = memberRepository;
		this.messageRepository = messageRepository;
		this.memberChatRoomRepository = memberChatRoomRepository;
		this.redisPublisher = redisPublisher;
		this.customStringRedisTemplate = customStringRedisTemplate;
		this.redisTemplate = redisTemplate;
		this.applicationEventPublisher = applicationEventPublisher;
		this.chatHandler = chatHandler;
	}

	/**
	 * chats/message에 프론트에서 입력된 메세지를 발행
	 * @param messageDto
	 */
	@Transactional
	public void sendMessage(MessageDto messageDto, String email) {
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		ChatRoom chatRoom = chatRoomRepository.findById(messageDto.getRoomId())
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.CHAT_ROOM_NOT_FOUND));

		Member opponent = chatRoom.checkMember(me);
		Message message = new Message(messageDto);
		messageRepository.save(message);

		String webSocketSessionId = customStringRedisTemplate.opsForValue().get(email);

		if (redisTemplate.opsForSet().members(webSocketSessionId) == null) {
			applicationEventPublisher.publishEvent(new NotifyChatEvent(this, me, opponent));
		}

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

		return chatHandler.mapResponseChatRoom(
			memberChatRoomRepository.findAllByMemberIdAndDeletedFalse(
				me.getId(), pageable));
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

	@Transactional
	public void markMessagesAsRead(ChatEnterRequest chatEnterRequest, String email) {
		Long messageId = chatEnterRequest.getMessageId();
		Message message = messageRepository.findById(messageId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MESSAGE_NOT_FOUNT));
		message.updateIsRead();
		messageRepository.save(message);
	}
}
