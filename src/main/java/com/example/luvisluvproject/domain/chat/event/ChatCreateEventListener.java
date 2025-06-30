package com.example.luvisluvproject.domain.chat.event;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.chat.dto.RoomIdDto;
import com.example.luvisluvproject.domain.chat.entity.ChatRoom;
import com.example.luvisluvproject.domain.chat.entity.MemberChatRoom;
import com.example.luvisluvproject.domain.chat.repository.ChatRoomRepository;
import com.example.luvisluvproject.domain.chat.repository.MemberChatRoomRepository;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor

public class ChatCreateEventListener {

	private final ChatRoomRepository chatRoomRepository;
	private final MemberRepository memberRepository;
	private final MemberChatRoomRepository memberChatRoomRepository;
	private final SimpMessageSendingOperations simpMessageSendingOperations;

	@Async
	@EventListener
	@Transactional
	public void handlerChatCreateEvent(ChatCreateEvent chatCreateEvent) {

		//멤버a찾고
		Long senderId = chatCreateEvent.getSenderId();
		Member member = memberRepository.findById(senderId).orElseThrow(() -> new CustomRuntimeException(
			ExceptionCode.USER_CANT_FIND));

		//나 찾고
		Long myId = chatCreateEvent.getMyId();
		Member me = memberRepository.findById(myId).orElseThrow(() -> new CustomRuntimeException(
			ExceptionCode.USER_CANT_FIND));

		//chatRoom저장
		ChatRoom chatRoom = chatRoomRepository.save(new ChatRoom(member, me));

		MemberChatRoom memberChatRoom = new MemberChatRoom(me.getId(), false, chatRoom);
		MemberChatRoom otherSide = new MemberChatRoom(member.getId(), false, chatRoom);
		memberChatRoomRepository.save(otherSide);
		memberChatRoomRepository.save(memberChatRoom);

		//roomId 프론트로 패스하기
		RoomIdDto SenderRoomIdDto = new RoomIdDto(chatRoom.getId(), me.getName(), "매칭이 성사되었습니다.", member.getId());
		RoomIdDto MeRoomIdDto = new RoomIdDto(chatRoom.getId(), member.getName(), "매칭이 성사되었습니다.", me.getId());

		simpMessageSendingOperations.convertAndSend("/sub/chat/init/" + senderId, SenderRoomIdDto);
		simpMessageSendingOperations.convertAndSend("/sub/chat/init/" + myId, MeRoomIdDto);
	}
}
