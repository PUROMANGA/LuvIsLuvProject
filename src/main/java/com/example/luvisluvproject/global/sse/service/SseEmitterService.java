package com.example.luvisluvproject.global.sse.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.notify.dto.NotifyDto;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SseEmitterService {
	private final Map<Long, SseEmitter> emitterMap;
	private final MemberRepository memberRepository;

	private static final long TIMEOUT = 60 * 1000;
	private static final long RECONNECTION_TIMEOUT = 1000L;

	public SseEmitter subscribe(String email) {

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		SseEmitter emitter = createEmitter();

		//타임 아웃되면 끊음
		emitter.onTimeout(() -> {
			emitter.complete();
		});

		//에러나면 끊음
		emitter.onError(e -> {
			emitter.complete();
		});

		//정상종료되면 의미없는 데이터 수거
		emitter.onCompletion(() -> {
			emitterMap.remove(member.getId());
		});

		emitterMap.put(member.getId(), emitter);

		return emitter;
	}

	public void sendToClient(Long receivedId, NotifyDto notifyDto) {
		SseEmitter emitter = emitterMap.get(receivedId);
		if(emitter != null) {
			try {
				emitter.send(SseEmitter.event()
					.name("notify")
					.data(notifyDto));
			} catch (IOException e) {
				emitterMap.remove(receivedId);
				throw new RuntimeException(e);
			}
		}
	}

	public SseEmitter createEmitter() {
		return new SseEmitter(TIMEOUT);
	}
}
