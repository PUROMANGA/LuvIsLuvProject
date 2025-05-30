package com.example.luvisluvproject.domain.chat.common;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.chat.entity.ChatRoom;
import com.example.luvisluvproject.domain.chat.repository.ChatRoomRepository;
import com.example.luvisluvproject.domain.chat.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j

public class StompHandler implements ChannelInterceptor {

	// private final JwtTokenProvider jwtTokenProvider;
	private final ChatService chatService;
	private final ChatRoomRepository chatRoomRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		if(StompCommand.CONNECT == accessor.getCommand()) {
			String authToken = accessor.getFirstNativeHeader("Authorization");
			String token = authToken.substring(7);

			// if(!jwtTokenProvider.validateToken(token)) {
			// 	throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
			// }
			//
			// String username = jwtTokenProvider.getUserNameFromToken(token);
			// accessor.setUser(() -> username);

		} else if(StompCommand.SUBSCRIBE == accessor.getCommand()) {

			String destination = accessor.getDestination();

			//세션아이디
			String sessionId = accessor.getSessionId();

			//방 아이디
			Long roomId = chatService.submitRoomId(destination);
			ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("채팅방이 존재하지 않습니다."));

			//Redis-key
			String key = "entryChatRoom : " + sessionId + ":" + roomId;
			String value = sessionId + ":" + roomId;
			//세션과 방 아이디를 저장
			redisTemplate.opsForSet().add(key, value);

			//입장 메세지 전송
			com.example.luvisluvproject.domain.chat.entity.Message entryMessage = com.example.luvisluvproject.domain.chat.entity.Message.builder()
				.messageType(MessageType.ENTER)
				.content("욕설은 제제 대상이 될 수 있습니다. 매너를 지켜서 대화해주세요.")
				.chatRoom(chatRoom)
				.senderId(0L)
				.build();

			chatService.sendChatEnterMessage(entryMessage);

		} else if (StompCommand.DISCONNECT == accessor.getCommand()) {
			// Websocket 연결 종료

			String destination = accessor.getDestination();

			//세션아이디
			String sessionId = accessor.getSessionId();

			//방 아이디
			Long roomId = chatService.submitRoomId(destination);

			String key = "entryChatRoom : " + sessionId + ":" + roomId;
			redisTemplate.delete(key);
		}
		return message;
	}
}
