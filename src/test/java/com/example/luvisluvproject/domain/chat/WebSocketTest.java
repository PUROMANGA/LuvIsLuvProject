package com.example.luvisluvproject.domain.chat;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.example.luvisluvproject.domain.auth.dto.request.LoginRequestDto;
import com.example.luvisluvproject.domain.auth.dto.response.LoginResponseDto;
import com.example.luvisluvproject.domain.auth.service.AuthService;
import com.example.luvisluvproject.domain.chat.dto.MessageDto;
import com.example.luvisluvproject.domain.chat.repository.ChatRoomRepository;
import com.example.luvisluvproject.domain.chat.repository.MemberChatRoomRepository;
import com.example.luvisluvproject.domain.match.dto.AcceptMatchDto;
import com.example.luvisluvproject.domain.match.entity.MatchStatus;
import com.example.luvisluvproject.domain.match.repository.MatchRepository;
import com.example.luvisluvproject.domain.match.service.MatchService;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.enums.UserRole;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.tag.repository.MemberTagRepository;
import com.example.luvisluvproject.global.config.JwtUtil;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;
import com.fasterxml.jackson.core.JsonProcessingException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest {

	@LocalServerPort
	private int port;

	private WebSocketStompClient webSocketStompClient;
	private StompSession stompSession;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MatchService matchService;

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Autowired
	private AuthService authService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private MemberChatRoomRepository memberChatRoomRepository;

	@BeforeAll
	public void setUp() {
		memberChatRoomRepository.deleteAll();
		chatRoomRepository.deleteAll();
		memberRepository.deleteAll();
		matchRepository.deleteAll();

		this.webSocketStompClient = new WebSocketStompClient(
			new StandardWebSocketClient()
		);

		this.webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

		webSocketStompClient = new WebSocketStompClient(new StandardWebSocketClient());
		webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

		String pw1 = passwordEncoder.encode("test1234");
		String pw2 = passwordEncoder.encode("test5678");

		Member senderMember = new Member(
			"송진영",
			"songjinyong@email.com",
			pw1,
			LocalDate.parse("2001-01-01"),
			UserRole.USER,
			true,
			1L
		);

		Member receiverMember = new Member(
			"이유리",
			"leeyuuri@email.com",
			pw2,
			LocalDate.parse("2001-01-01"),
			UserRole.USER,
			true,
			1L
		);

		memberRepository.save(senderMember);
		memberRepository.save(receiverMember);
	}

	@Test
	@DisplayName("Stomp의 Connect됐을 때 테스트")
	public void testWebSocket() throws ExecutionException, InterruptedException, JsonProcessingException {

		AcceptMatchDto acceptMatchDto = new AcceptMatchDto(MatchStatus.ACCEPTED);
		String url = "ws://localhost:" + port + "/ws";

		String myToken = jwtUtil.createAccessToken("leeyuuri@email.com", "USER");

		matchService.createMatchService(2L, "songjinyong@email.com");
		matchService.patchMatchService(1L, acceptMatchDto, "leeyuuri@email.com");

		WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
		webSocketHttpHeaders.add("Authorization", "Bearer " + myToken);

		StompHeaders stompHeaders = new StompHeaders();
		stompHeaders.add("Authorization", "Bearer " + myToken);

		stompSession = webSocketStompClient.connectAsync(
			url,
			webSocketHttpHeaders,
			stompHeaders,
			new StompSessionHandlerAdapter() {
				@Override
				public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
					System.out.println("STOMP 연결 성공");
				}
			}).get();
	}

	@Test
	@DisplayName("실제로 메세지 보내기")
	public void testWebSocketWhenSub() throws InterruptedException, ExecutionException, JsonProcessingException {

		//큐 구조 설정
		BlockingQueue<MessageDto> queue = new LinkedBlockingQueue<>();

		//보내는 메세지 정하기
		MessageDto messageDto = new MessageDto(
			"안녕하세유~",
			null,
			1L,
			1L,
			"송진영"
		);

		//매칭 수락하기
		AcceptMatchDto acceptMatchDto = new AcceptMatchDto(MatchStatus.ACCEPTED);

		//로그인 할 때 정보
		LoginRequestDto requestDto = new LoginRequestDto(
			"leeyuuri@email.com",
			"test5678");

		//로그인 시도
		LoginResponseDto loginResponseDto = authService.login(requestDto);
		System.out.println("로그인 성공");
		matchService.createMatchService(2L, "songjinyong@email.com");
		System.out.println("매칭 신청 성공 해당 정보");
		matchService.patchMatchService(1L, acceptMatchDto, "leeyuuri@email.com");
		System.out.println("매칭 허락, 채팅방 만들기");

		Member memberB = memberRepository.findByEmail("leeyuuri@email.com")
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		StompSession receiveSession = connectWebSocket(memberB);

		receiveSession.subscribe("/sub/chats/" + 1L, new StompFrameHandler() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				return MessageDto.class;
			}

			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				MessageDto sendMessageDto = (MessageDto)payload;
				queue.offer(sendMessageDto);
				System.out.println("받은 메세지 = " + sendMessageDto);
			}
		});
		System.out.println("구독완료");

		StompHeaders stompHeaders = new StompHeaders();
		stompHeaders.setDestination("/pub/chats/message");
		// stompHeaders.add("Authorization", loginResponseDto.getAccessToken());

		System.out.println("메세지발송 전");
		receiveSession.send(stompHeaders, messageDto);

		System.out.println("메세지발송 후");
		System.out.println("messageDto = " + messageDto);

		MessageDto received = queue.poll(5, TimeUnit.SECONDS);
		System.out.println("큐 사이즈: " + queue.size());
		assertThat(received.getContent()).isEqualTo("안녕하세유~");
	}

	private StompSession connectWebSocket(Member member) throws
		InterruptedException, ExecutionException {
		String url = "ws://localhost:" + port + "/ws";

		String myToken = jwtUtil.createAccessToken("leeyuuri@email.com", "USER");

		WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
		webSocketHttpHeaders.add("Authorization", "Bearer " + myToken);

		StompHeaders stompHeaders = new StompHeaders();
		stompHeaders.add("Authorization", "Bearer " + myToken);

		return stompSession = webSocketStompClient.connectAsync(
			url,
			webSocketHttpHeaders,
			stompHeaders,
			new StompSessionHandlerAdapter() {
				@Override
				public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
					System.out.println("STOMP 연결 성공");
				}
			}
		).get();
	}
}
