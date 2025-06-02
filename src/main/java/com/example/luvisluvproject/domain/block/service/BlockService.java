package com.example.luvisluvproject.domain.block.service;

import com.example.luvisluvproject.domain.block.dto.BlockRequestDto;
import com.example.luvisluvproject.domain.block.dto.BlockResponseDto;
import com.example.luvisluvproject.domain.block.entity.Block;
import com.example.luvisluvproject.domain.block.repository.BlockRepository;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockService {

	private final BlockRepository blockRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public BlockResponseDto blockUser(Long userId, BlockRequestDto requestDto) {
		Long blockedId = requestDto.getBlockedId();

		if (userId.equals(blockedId)) {
			throw new CustomRuntimeException(ExceptionCode.CANNOT_BLOCK_SELF);
		}

		Member blocker = memberRepository.findById(userId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		Member blocked = memberRepository.findById(blockedId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		if (blockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
			throw new CustomRuntimeException(ExceptionCode.ALREADY_BLOCKED);
		}

		Block block = Block.builder()
			.blocker(blocker)
			.blocked(blocked)
			.blockDM(requestDto.isBlockDM())
			.blockProfileAccess(requestDto.isBlockProfileAccess())
			.excludeFromMatching(requestDto.isExcludeFromMatching())
			.excludeFromRecommendation(requestDto.isExcludeFromRecommendation())
			.blockType(Block.BlockType.valueOf(requestDto.getBlockType()))
			.build();

		blockRepository.save(block);

		return new BlockResponseDto("사용자를 차단했습니다.", blockedId, block.getCreatTime());
	}

	@Transactional
	public String unblockUser(Long userId, Long blockedId) {
		Member blocker = memberRepository.findById(userId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		Member blocked = memberRepository.findById(blockedId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		Block block = blockRepository.findByBlockerAndBlocked(blocker, blocked)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.BLOCK_NOT_FOUND));

		block.unblock(); // soft delete 처리
		return "차단을 해제했습니다.";
	}

	public List<String> getBlockedUsers(Long userId) {
		Member blocker = memberRepository.findById(userId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		return blockRepository.findAllByBlocker(blocker).stream()
			.filter(block -> !block.isUnblocked())
			.map(block -> block.getBlocked().getName() + "을(를) 차단 중입니다.")
			.collect(Collectors.toList());
	}

	public boolean isBlocked(Member viewer, Member target) {
		return blockRepository.existsByBlockerAndBlocked(viewer, target);
	}

	public Block getBlockInfo(Member viewer, Member target) {
		return blockRepository.findByBlockerAndBlocked(viewer, target)
			.orElse(null);
	}

	public boolean isExcludedFromMatching(Member requester, Member target) {
		Block block = blockRepository.findByBlockerAndBlocked(requester, target).orElse(null);
		return block != null && !block.isUnblocked() && block.isExcludeFromMatching();
	}
}
