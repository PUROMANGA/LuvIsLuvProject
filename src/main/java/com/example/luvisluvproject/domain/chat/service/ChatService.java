package com.example.luvisluvproject.domain.chat.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.chat.dto.RequestMessageDto;
import com.example.luvisluvproject.domain.chat.dto.ResponseChatRoom;
import com.example.luvisluvproject.domain.chat.dto.ResponseChatRoomCount;
import com.example.luvisluvproject.domain.chat.dto.ResponseMessageDto;
import com.example.luvisluvproject.domain.chat.entity.ChatRoom;
import com.example.luvisluvproject.domain.chat.entity.MemberChatRoom;
import com.example.luvisluvproject.domain.chat.entity.Message;
import com.example.luvisluvproject.domain.chat.repository.ChatRoomRepository;
import com.example.luvisluvproject.domain.chat.repository.MemberChatRoomRepository;
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
	private final MemberChatRoomRepository memberChatRoomRepository;

	/**
	 * /pub/chats/chatID로 메세지가 보내지면 /sub/chats/chatId로 convertAndSend를 사용해서 메세지를 전달하게 됩니다.
	 * @param requestMessageDto
	 * @param chatId
	 * @param email
	 */
	@Transactional
	public void sendChatMessage(RequestMessageDto requestMessageDto, Long chatId, String email) {
		Boolean isRead = false;
		simpMessagingTemplate.convertAndSend("/sub/chats/" + chatId, requestMessageDto);
		ChatRoom chatRoom = chatRoomRepository.findById(chatId).orElseThrow(() -> new RuntimeException("채팅방이 없습니다."));
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		Member opponent = chatRoom.checkMember(me);
		Message message = new Message(chatId, me.getId(), opponent.getId(), requestMessageDto, isRead);
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
		Slice<Message> messageList = messageRepository.findAllByChatRoomId(chatId, pageable);
		ChatRoom chatRoom = chatRoomRepository.findById(chatId).orElseThrow(() -> new RuntimeException("채팅방이 없습니다."));
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		List<ResponseMessageDto> responseMessageDtoList = messageList.stream()

			.map(ResponseMessageDto::new)
			.toList();
		return new SliceImpl<>(responseMessageDtoList, pageable, messageList.hasNext());
	}

	/**
	 * 내가 메세지를 봤는데 내가 보낸 게 아니라면 읽음 표시를 합니다.
	 * @param messageId
	 * @param member
	 */

	@Transactional
	public void updateIsReadService(Long messageId, String email) {
		Message message = messageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("메세지가 없습니다."));
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		if (!message.getSenderId().equals(me.getId())) {
			message.updateIsRead();
			messageRepository.save(message);
		}
	}

	/**
	 * member 확인해주면서 채팅방 삭제
	 * @param email
	 * @param chatId
	 */
	@Transactional
	public void deleteChatRoomService(String email, Long chatId) {
		ChatRoom chatRoom = chatRoomRepository.findById(chatId).orElseThrow(() -> new RuntimeException("채팅방이 없습니다."));
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		if (me.getEmail().equals(chatRoom.getMemberB().getEmail())) {
			chatRoomRepository.delete(chatRoom);
		} else {
			throw new RuntimeException("자격이 없습니다");
		}
	}

	/**
	 * 해당 사용자가 들어가있는 채팅방을 전부 다 불러옵니다.
	 * @param email
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public Slice<ResponseChatRoom> getAllChatRoomService(String email, Pageable pageable) {
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		List<MemberChatRoom> memberChatRoomList = memberChatRoomRepository.findAllByMemberId(me.getId());
		List<ResponseChatRoom> responseChatRoomList = new ArrayList<>();
		for (MemberChatRoom memberChatRoom : memberChatRoomList) {
			ChatRoom chatRoom = memberChatRoom.getChatRoom();
			Member member = chatRoom.checkMember(me);
			Message message = messageRepository.findByChatRoomId(chatRoom.getId())
				.orElseThrow(() -> new RuntimeException("메세지가 없습니다."));
			ResponseChatRoom responseChatRoom = new ResponseChatRoom(memberChatRoom.getChatRoom().getId(),
				member.getName(), message.getContent());
			responseChatRoomList.add(responseChatRoom);
		}
		int start = (int)pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize() + 1, responseChatRoomList.size());
		List<ResponseChatRoom> sliceList = responseChatRoomList.subList(start, end);
		boolean hasNext = sliceList.size() > pageable.getPageSize();
		List<ResponseChatRoom> content = hasNext ? sliceList.subList(0, pageable.getPageSize()) : sliceList;
		return new SliceImpl<>(content, pageable, hasNext);
	}

	/**
	 * 해당 사용자가 들어있는 채팅방을 전부 다 셉니다.
	 * @param email
	 * @return
	 */
	@Transactional(readOnly = true)
	public ResponseChatRoomCount getAllChatCount(String email) {
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		int countChatRoom = memberChatRoomRepository.countMemberChatRoomsByMemberId(me.getId());

		return new ResponseChatRoomCount(countChatRoom);
	}
}
