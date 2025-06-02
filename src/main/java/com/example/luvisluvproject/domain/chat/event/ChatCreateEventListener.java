package com.example.luvisluvproject.domain.chat.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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


	@Async
	@EventListener
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

		Member opponent;

		if(me.getId().equals(chatRoom.getMemberA().getId())) {
			opponent = chatRoom.getMemberB();

		} else {
			opponent = chatRoom.getMemberA();
		}
		String chatName = opponent.getName();
		MemberChatRoom memberChatRoom = new MemberChatRoom(me.getId(), chatName, chatRoom);
		memberChatRoomRepository.save(memberChatRoom);
	}
}
