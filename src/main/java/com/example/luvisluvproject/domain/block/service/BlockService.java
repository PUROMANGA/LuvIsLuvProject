package com.example.luvisluvproject.domain.block.service;

import com.example.luvisluvproject.domain.block.dto.*;
import com.example.luvisluvproject.domain.block.entity.Block;
import com.example.luvisluvproject.domain.block.repository.BlockRepository;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BlockService {

	private final BlockRepository blockRepository;
	private final MemberRepository memberRepository;

	/**
	 * 사용자를 차단합니다.
	 */
	public BlockResponseDto blockUser(String email, BlockRequestDto dto) {
		// 요청자 정보 조회
		Member blocker = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		// 차단 대상 정보 조회
		Member blocked = memberRepository.findByEmail(dto.getBlockedEmail())
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		// 자기 자신 차단 방지
		if (blocker.getEmail().equals(blocked.getEmail())) {
			throw new CustomRuntimeException(ExceptionCode.CANNOT_BLOCK_SELF);
		}

		// 이미 차단된 경우 중복 방지
		if (blockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
			throw new CustomRuntimeException(ExceptionCode.ALREADY_BLOCKED);
		}

		// 차단 생성
		Block block = Block.builder()
			.blocker(blocker)
			.blocked(blocked)
			.blockUserAccess(dto.isBlockUserAccess())
			.excludeFromMatching(dto.isExcludeFromMatching())
			.blockType(dto.getBlockType())
			.build();

		blockRepository.save(block);

		return new BlockResponseDto(blocked.getId());
	}

	/**
	 * 사용자를 차단 해제합니다.
	 */
	public UnblockResponseDto unblockUser(String email, String blockedEmail) {
		Member blocker = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		Member blocked = memberRepository.findByEmail(blockedEmail)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		Block block = blockRepository.findByBlockerAndBlocked(blocker, blocked)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.BLOCK_NOT_FOUND));

		blockRepository.delete(block);

		return new UnblockResponseDto(blocked.getId());
	}

	/**
	 * 일반 사용자용: 본인 이메일 기준 차단 목록 조회
	 */
	public Slice<BlockUserDto> getBlockedUsersByEmail(String email, Pageable pageable) {
		Member blocker = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		return blockRepository.findAllByBlocker(blocker, pageable)
			.map(block -> new BlockUserDto(
				block.getBlocked().getId(),
				block.getBlocked().getName(),
				block.getBlocked().getEmail()
			));
	}

	/**
	 * 관리자용: 특정 사용자 ID 기준으로 차단 목록 조회
	 */
	public Slice<BlockUserDto> getBlockedUsersByUserId(Long userId, Pageable pageable) {
		Member blocker = memberRepository.findById(userId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		return blockRepository.findAllByBlocker(blocker, pageable)
			.map(block -> new BlockUserDto(
				block.getBlocked().getId(),
				block.getBlocked().getName(),
				block.getBlocked().getEmail()
			));
	}

	/**
	 * 프로필 접근 차단 여부 확인 (캐시 적용)
	 */
	public boolean isProfileBlocked(Member viewer, Member target) {
		Block block = blockRepository.findByBlockerAndBlocked(target, viewer).orElse(null);
		return block != null;
	}
}
