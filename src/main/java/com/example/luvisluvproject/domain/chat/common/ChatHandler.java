package com.example.luvisluvproject.domain.chat.common;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.chat.dto.ResponseChatRoom;
import com.example.luvisluvproject.domain.chat.entity.ChatRoom;
import com.example.luvisluvproject.domain.chat.entity.MemberChatRoom;
import com.example.luvisluvproject.domain.chat.entity.Message;
import com.example.luvisluvproject.domain.chat.mongorepository.MessageRepository;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatHandler {

	private final MemberRepository memberRepository;
	private final MessageRepository messageRepository;

	public Slice<ResponseChatRoom> mapResponseChatRoom(Slice<MemberChatRoom> memberChatRooms) {
		return memberChatRooms.map(memberChatRoom -> {
			Member findMe = memberRepository.findById(memberChatRoom.getMemberId())
				.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
			ChatRoom chatRoom = memberChatRoom.getChatRoom();
			Member opponent = chatRoom.checkMember(findMe);
			Message message = messageRepository.findFirstByChatRoomIdOrderByCreatTimeDesc(chatRoom.getId())
				.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MESSAGE_NOT_FOUNT));
			return new ResponseChatRoom(chatRoom.getId(), opponent.getName(), message.getContent());
		});

	}
}
