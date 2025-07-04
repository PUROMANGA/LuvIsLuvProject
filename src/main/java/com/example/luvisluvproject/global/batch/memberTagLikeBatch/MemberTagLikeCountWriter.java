package com.example.luvisluvproject.global.batch.memberTagLikeBatch;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberInteractionLog;
import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberTagLikeCount;
import com.example.luvisluvproject.domain.memberInteractionLog.repository.MemberInteractionLogRepository;
import com.example.luvisluvproject.domain.memberInteractionLog.repository.MemberTagLikeCountRepository;
import com.example.luvisluvproject.domain.notify.event.NotifyLogEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberTagLikeCountWriter implements ItemWriter<MemberTagLikeCount> {

	private final MemberTagLikeCountRepository memberTagLikeCountRepository;

	@Override
	public void write(Chunk<? extends MemberTagLikeCount> chunk) throws Exception {
		List<? extends MemberTagLikeCount> memberTagLikeCounts = chunk.getItems();
		memberTagLikeCountRepository.saveAll(memberTagLikeCounts);
	}
}
