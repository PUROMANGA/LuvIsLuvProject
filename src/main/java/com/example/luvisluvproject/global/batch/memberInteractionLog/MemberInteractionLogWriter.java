package com.example.luvisluvproject.global.batch.memberInteractionLog;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberInteractionLog;
import com.example.luvisluvproject.domain.memberInteractionLog.repository.MemberInteractionLogRepository;
import com.example.luvisluvproject.domain.notify.event.NotifyLogEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberInteractionLogWriter implements ItemWriter<MemberInteractionLog> {

	private final MemberInteractionLogRepository memberInteractionLogRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void write(Chunk<? extends MemberInteractionLog> chunk) throws Exception {
		List<? extends MemberInteractionLog> memberInteractionLogs = chunk.getItems();
		memberInteractionLogRepository.saveAll(memberInteractionLogs);

		for(MemberInteractionLog memberInteractionLog : memberInteractionLogs) {
			Long memberId = memberInteractionLog.getMemberId();
			applicationEventPublisher.publishEvent(new NotifyLogEvent(this, memberId));
		}
	}
}
