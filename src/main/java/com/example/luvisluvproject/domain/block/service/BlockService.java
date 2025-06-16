package com.example.luvisluvproject.domain.block.service;

import com.example.luvisluvproject.domain.block.dto.BlockRequestDto;
import com.example.luvisluvproject.domain.block.dto.BlockResponseDto;
import com.example.luvisluvproject.domain.block.dto.BlockUserDto;
import com.example.luvisluvproject.domain.block.dto.UnblockResponseDto;
import com.example.luvisluvproject.domain.block.entity.Block;
import com.example.luvisluvproject.domain.block.repository.BlockRepository;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockService {

	private final BlockRepository blockRepository;
	private final MemberRepository memberRepository;

	/**
	 * 사용자 차단
	 */
	@Transactional
	public BlockResponseDto blockUser(Long userId, BlockRequestDto dto) {
		if (userId.equals(dto.getBlockedId())) {
			throw new CustomRuntimeException(ExceptionCode.CANNOT_BLOCK_SELF);
		}

		Member blocker = memberRepository.findById(userId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		Member blocked = memberRepository.findById(dto.getBlockedId())
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		if (blockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
			throw new CustomRuntimeException(ExceptionCode.ALREADY_BLOCKED);
		}

		Block block = Block.builder()
			.blocker(blocker)
			.blocked(blocked)
			.blockUserAccess(dto.isBlockUserAccess())
			.blockType(dto.getBlockType())
			.build();

		blockRepository.save(block);

		return new BlockResponseDto("사용자를 차단했습니다.", blocked.getId(), LocalDateTime.now());
	}

	/**
	 * 사용자 차단 해제
	 */
	@Transactional
	public UnblockResponseDto unblockUser(Long userId, Long blockedId) {
		Member blocker = memberRepository.findById(userId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		Member blocked = memberRepository.findById(blockedId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		Block block = blockRepository.findByBlockerAndBlocked(blocker, blocked)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.BLOCK_NOT_FOUND));

		block.unblock();

		return new UnblockResponseDto(blocked.getId(), "차단을 해제했습니다.");
	}

	/**
	 * 차단한 사용자 목록 조회 // 어드민만 가능하게, 어노테이션 트랜잭셔널 붙이기
	 */
	public List<BlockUserDto> getBlockedUsers(Long userId) {
		Member blocker = memberRepository.findById(userId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		return blockRepository.findAllByBlocker(blocker).stream()
			.filter(block -> !block.isUnblocked())
			.map(block -> {
				Member target = block.getBlocked();
				return new BlockUserDto(target.getId(), target.getName(), target.getEmail());
			})
			.collect(Collectors.toList());
	}

	/**
	 * 상대방이 나를 차단했는지 여부 확인 (캐싱 적용)
	 */
	@Cacheable(value = "profileBlock", key = "#viewer.id + ':' + #target.id")
	public boolean isProfileBlocked(Member viewer, Member target) {
		Block block = blockRepository.findByBlockerAndBlocked(target, viewer).orElse(null);
		return block != null && !block.isUnblocked() && block.isBlockUserAccess();
	}
}
