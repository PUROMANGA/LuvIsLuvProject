package com.example.luvisluvproject.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.example.luvisluvproject.domain.chat.dto.RequestMessageDto;
import com.example.luvisluvproject.domain.chat.entity.ChatRoom;
import com.example.luvisluvproject.domain.chat.entity.MemberChatRoom;
import com.example.luvisluvproject.domain.chat.repository.ChatRoomRepository;
import com.example.luvisluvproject.domain.chat.repository.MessageRepository;
import com.example.luvisluvproject.domain.chat.service.ChatService;
import com.example.luvisluvproject.domain.match.dto.AcceptMatchDto;
import com.example.luvisluvproject.domain.match.entity.MatchStatus;
import com.example.luvisluvproject.domain.match.repository.MatchRepository;
import com.example.luvisluvproject.domain.match.service.MatchService;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.enums.UserRole;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
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
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private MatchService matchService;

	@Autowired
	private ChatService chatService;

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private MatchRepository matchRepository;

	@BeforeAll
	public void setUp() {
		chatRoomRepository.deleteAll();
		memberRepository.deleteAll();
		matchRepository.deleteAll();

		webSocketStompClient = new WebSocketStompClient(
			new StandardWebSocketClient()
		);

		webSocketStompClient = new WebSocketStompClient(new StandardWebSocketClient());
		webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

		Member senderMember = new Member(
			"송진영",
			"songjinyong@email.com",
			"test1234",
			LocalDate.parse("2001-01-01"),
			UserRole.USER,
			true,
			1L
		);

		Member receiverMember = new Member(
			"이유리",
			"leeyuuri@email.com",
			"test5678",
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
		BlockingQueue<RequestMessageDto> queue = new LinkedBlockingQueue<>();

		//보내는 메세지 정하기
		RequestMessageDto requestMessageDto = new RequestMessageDto(
			"안녕하세유~",
			null
		);

		//실제 구독
		AcceptMatchDto acceptMatchDto = new AcceptMatchDto(MatchStatus.ACCEPTED);
		matchService.createMatchService(2L, "songjinyong@email.com");
		matchService.patchMatchService(1L, acceptMatchDto, "leeyuuri@email.com");

		Member memberB = memberRepository.findByEmail("leeyuuri@email.com")
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		StompSession receiveSession = connectWebSocket(memberB);

		receiveSession.subscribe("/sub/chats/1", new StompFrameHandler() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				return RequestMessageDto.class;
			}

			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				RequestMessageDto requestMessageDto = (RequestMessageDto)payload;
				queue.offer(requestMessageDto);
				System.out.println("받은 메세지 = " + requestMessageDto);
			}
		});

		StompHeaders stompHeaders = new StompHeaders();
		stompHeaders.setDestination("/sub/chats/1");
		receiveSession.send(stompHeaders, requestMessageDto);
		RequestMessageDto received = queue.poll(5, TimeUnit.SECONDS);
		assertThat(received).isNotNull();
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
