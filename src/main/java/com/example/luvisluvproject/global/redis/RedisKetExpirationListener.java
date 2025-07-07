package com.example.luvisluvproject.global.redis;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.admin.sanction.entity.Sanction;
import com.example.luvisluvproject.admin.sanction.repository.SanctionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class RedisKetExpirationListener implements MessageListener {

	private final SanctionRepository sanctionRepository;

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String expiredKey = new String(message.getBody(), StandardCharsets.UTF_8);

		if(expiredKey.startsWith("ban:")) {
			Long memberId = Long.valueOf(expiredKey.replace("ban:", ""));

			Optional<Sanction> sanction = sanctionRepository.findByMemberId(memberId);

			if(sanction.isEmpty()) {
				throw new RuntimeException("이 유저는 밴 당하지 않았습니다.");
			}
			sanction.get().updateStatus();
			sanctionRepository.save(sanction.get());

		}
	}
}
