package com.example.luvisluvproject.domain.block.service;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockService {

	private final MemberRepository memberRepository;

	@Transactional
	public String blockUser(Long userId, Long blockedId) {
		if (userId.equals(blockedId)) {
			throw new CustomRuntimeException(ExceptionCode.CANNOT_BLOCK_SELF);
		}

		Member blocker = memberRepository.findById(userId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		Member blocked = memberRepository.findById(blockedId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		// 실제 차단은 저장하지 않음. UI 차원에서만 처리 가능.
		return "사용자를 차단한 것으로 간주합니다 (저장 안됨)";
	}

	@Transactional
	public String unblockUser(Long userId, Long blockedId) {
		// 데이터가 없으므로 그냥 정상 응답
		return "차단 해제가 완료되었습니다 (저장된 데이터 없음)";
	}

	public List<String> getBlockedUsers(Long userId) {
		// 차단 정보가 없으므로 빈 리스트 반환
		return Collections.emptyList();
	}
}
