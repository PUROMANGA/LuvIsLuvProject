// package com.example.luvisluvproject.domain.chat;
//
// import static org.assertj.core.api.Assertions.assertThat;
//
// import java.time.LocalDate;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Slice;
// import org.springframework.data.domain.Sort;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.example.luvisluvproject.domain.chat.dto.ResponseChatRoom;
// import com.example.luvisluvproject.domain.chat.entity.ChatRoom;
// import com.example.luvisluvproject.domain.chat.entity.MemberChatRoom;
// import com.example.luvisluvproject.domain.chat.entity.Message;
// import com.example.luvisluvproject.domain.chat.mongorepository.MessageRepository;
// import com.example.luvisluvproject.domain.chat.repository.ChatRoomRepository;
// import com.example.luvisluvproject.domain.chat.repository.MemberChatRoomRepository;
// import com.example.luvisluvproject.domain.member.entity.Member;
// import com.example.luvisluvproject.domain.member.enums.UserRole;
// import com.example.luvisluvproject.domain.member.repository.MemberRepository;
// import com.example.luvisluvproject.global.error.CustomRuntimeException;
// import com.example.luvisluvproject.global.error.ExceptionCode;
//
// @SpringBootTest
//
// public class ChatMessageTest {
//
// 	@Autowired
// 	private MemberRepository memberRepository;
//
// 	@Autowired
// 	private MessageRepository messageRepository;
//
// 	@Autowired
// 	private ChatRoomRepository chatRoomRepository;
// 	@Autowired
// 	private MemberChatRoomRepository memberChatRoomRepository;
//
// 	@BeforeEach
// 	void setUp() {
// 		//재시작하면서 db 날리기
// 		chatRoomRepository.deleteAll();
// 		memberChatRoomRepository.deleteAll();
// 		memberRepository.deleteAll();
//
// 		//테스트용 멤버 객체 만들어주고 member db에 저장
// 		Member me = new Member(
// 			"송진영",
// 			"songjinyong@email.com",
// 			"test1234",
// 			LocalDate.parse("2001-01-01"),
// 			UserRole.USER,
// 			true,
// 			1L
// 		);
//
// 		Member receiverMember = new Member(
// 			"이유리",
// 			"leeyuuri@email.com",
// 			"test5678",
// 			LocalDate.parse("2001-01-01"),
// 			UserRole.USER,
// 			true,
// 			1L
// 		);
//
// 		Member member3 = new Member(
// 			"방명수",
// 			"parkmyonsu@email.com",
// 			"test91011",
// 			LocalDate.parse("2001-01-01"),
// 			UserRole.USER,
// 			true,
// 			1L
// 		);
//
// 		memberRepository.save(me);
// 		memberRepository.save(receiverMember);
// 		memberRepository.save(member3);
//
// 		//chatRoom 미리 만들어주기
// 		ChatRoom chatRoom = new ChatRoom(
// 			me,
// 			receiverMember,
// 			0
// 		);
//
// 		ChatRoom chatRoom2 = new ChatRoom(
// 			receiverMember,
// 			member3,
// 			0
// 		);
//
// 		ChatRoom chatRoom3 = new ChatRoom(
// 			me,
// 			member3,
// 			0
// 		);
//
// 		chatRoomRepository.save(chatRoom);
// 		createChatRoom(me.getId(), chatRoom);
// 	}
//
// 	@Transactional
// 	@Test
// 	@DisplayName("송진영 입장에서 채팅방 다 가져오기")
// 	public void getAllChatRoomServiceTest() {
// 		//given
// 		String senderEmail = "songjinyong@email.com";
//
// 		//나를 찾고
// 		Member me = memberRepository.findByEmail(senderEmail)
// 			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
//
// 		//페이지 객체 만들어주기
// 		int page = 0;
// 		int size = 10;
// 		Sort sort = Sort.by(Sort.Direction.DESC, "creatTime");
// 		Pageable pageable = PageRequest.of(page, size, sort);
//
// 		//MemberChatRoom 찾아주고
// 		Slice<ResponseChatRoom> memberChatRoomList = memberChatRoomRepository.findAllByMemberIdAndDeletedFalse(me.getId(), pageable)
// 			.map(memberChatRoom  -> {
// 				Member findMe = memberRepository.findById(memberChatRoom.getMemberId()).orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
// 				ChatRoom chatRoom = memberChatRoom.getChatRoom();
// 				Member opponent =  chatRoom.checkMember(findMe);
// 				Message message = messageRepository.findFirstByChatRoomIdOrderByCreatTimeDesc(chatRoom.getId()).orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MESSAGE_NOT_FOUNT));
// 				return new ResponseChatRoom(chatRoom.getId(), opponent.getName(), message.getContent());
// 			});
//
// 		for(ResponseChatRoom responseChatRoom : memberChatRoomList) {
// 			assertThat(responseChatRoom.getMemberName()).isEqualTo("이유리");
// 		}
// 	}
//
// 	public void createChatRoom(Long memberId, ChatRoom chatRoom) {
// 		MemberChatRoom memberChatRoom = new MemberChatRoom(
// 			memberId, chatRoom
// 		);
// 		memberChatRoomRepository.save(memberChatRoom);
// 	}
// }
