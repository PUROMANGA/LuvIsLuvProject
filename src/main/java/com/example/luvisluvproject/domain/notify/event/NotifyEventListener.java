package com.example.luvisluvproject.domain.notify.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.notify.NotifyCategory;
import com.example.luvisluvproject.domain.notify.dto.NotifyDto;
import com.example.luvisluvproject.domain.notify.entity.Notify;
import com.example.luvisluvproject.domain.notify.repository.NotifyRepository;
import com.example.luvisluvproject.global.redis.RedisPublisher;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotifyEventListener {

	private final NotifyRepository notifyRepository;
	private final RedisPublisher redisPublisher;

	@Async
	@EventListener
	@Transactional
	public void handlerNotifyMatchEventListener(NotifyMatchEvent notifyMatchEventEvent) {
		Member me = notifyMatchEventEvent.getMember();
		Member opponent = notifyMatchEventEvent.getOpponent();

		NotifyDto notifyDto = new NotifyDto(me, opponent, NotifyCategory.SUCCESS_MATCH, false);
		Notify notify = new Notify(notifyDto, opponent, me);
		notifyRepository.save(notify);
		redisPublisher.publishNotify(notifyDto);
	}

	@Async
	@EventListener
	@Transactional
	public void handlerNotifyMatchSubEventListener(NotifyMatchSubEvent notifyMatchSubEvent) {
		Member me = notifyMatchSubEvent.getMember();
		Member opponent = notifyMatchSubEvent.getOpponent();

		NotifyDto notifyDto = new NotifyDto(me, opponent, NotifyCategory.Match, false);
		Notify notify = new Notify(notifyDto, opponent, me);
		notifyRepository.save(notify);
		redisPublisher.publishNotify(notifyDto);
	}

	@Async
	@EventListener
	@Transactional
	public void handlerNotifyChatEventListener(NotifyChatEvent notifyChatEvent) {
		Member me = notifyChatEvent.getMember();
		Member opponent = notifyChatEvent.getOpponent();

		NotifyDto notifyDto = new NotifyDto(me, opponent, NotifyCategory.Message, false);
		Notify notify = new Notify(notifyDto, opponent, me);
		notifyRepository.save(notify);
		redisPublisher.publishNotify(notifyDto);
	}
}
