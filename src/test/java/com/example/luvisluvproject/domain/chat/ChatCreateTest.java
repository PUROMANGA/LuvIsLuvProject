// package com.example.luvisluvproject.domain;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
//
// import com.example.luvisluvproject.domain.chat.common.MessageType;
// import com.example.luvisluvproject.domain.chat.dto.RequestMessageDto;
// import com.example.luvisluvproject.domain.chat.entity.ChatRoom;
// import com.example.luvisluvproject.domain.chat.entity.MemberChatRoom;
// import com.example.luvisluvproject.domain.chat.entity.Message;
// import com.example.luvisluvproject.domain.chat.mongorepository.MessageRepository;
// import com.example.luvisluvproject.domain.chat.repository.ChatRoomRepository;
// import com.example.luvisluvproject.domain.chat.service.ChatService;
// import com.example.luvisluvproject.domain.member.entity.Member;
// import com.example.luvisluvproject.domain.member.enums.UserRole;
// import com.example.luvisluvproject.domain.member.repository.MemberRepository;
//
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.BDDMockito.given;
// import static org.mockito.Mockito.*;
//
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;
//
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @ExtendWith(MockitoExtension.class)
//
// public class ChatCreateTest {
//
// 	@Mock
// 	private SimpMessagingTemplate simpMessagingTemplate;
//
// 	@Mock
// 	private ChatRoomRepository chatRoomRepository;
//
// 	@Mock
// 	private MemberRepository memberRepository;
//
// 	@Mock
// 	private MessageRepository messageRepository;
//
// 	@InjectMocks
// 	private ChatService chatService;
//
// 	Member memberA = new Member(
// 		1L,
// 		"송진영",
// 		"songjinyong@email.com",
// 		"test1234",
// 		LocalDate.parse("2001-01-01"),
// 		UserRole.USER,
// 		true,
// 		1L
// 	);
//
// 	Member memberB = new Member(
// 		2L,
// 		"이유리",
// 		"leeyuuri@email.com",
// 		"test5678",
// 		LocalDate.parse("2001-02-01"),
// 		UserRole.USER,
// 		true,
// 		1L
// 	);
//
// 	ChatRoom chatRoom = new ChatRoom(
// 		1L,
// 		memberA,
// 		memberB,
// 		0
// 	);
//
// 	Boolean isRead = false;
//
// 	Message message = new Message(
// 		"1",
// 		1L,
// 		memberB.getId(),
// 		"아아아아아아",
// 		"대충 주소",
// 		MessageType.IMAGE,
// 		false,
// 		LocalDateTime.now(),
// 		LocalDateTime.now()
// 	);
//
// 	@Test
// 	@DisplayName("채팅방에 들어있는 메세지를 전부 들고오고, 내가 아닌 사람이 보낸 메세지를 읽음으로 표시합니다.")
// 	public void sendChatMessageTest() {
//
// 		//given
// 		given(chatRoomRepository.findById(anyLong())).willReturn(Optional.of(chatRoom));
// 		given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(memberB));
// 		given(messageRepository.save(any(Message.class))).willReturn(message);
//
// 		RequestMessageDto requestMessageDto = new RequestMessageDto(
// 			"대충 내용",
// 			null
// 		);
//
// 		//when
// 		chatService.sendChatMessage(requestMessageDto, chatRoom.getId(), memberB.getEmail());
//
// 		//then
// 		verify(simpMessagingTemplate, times(1)).convertAndSend(eq("/sub/chats/" + chatRoom.getId()),
// 			eq(requestMessageDto));
// 	}
// }
