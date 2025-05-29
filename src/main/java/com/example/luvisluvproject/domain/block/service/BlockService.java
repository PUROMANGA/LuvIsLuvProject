package com.example.luvisluvproject.domain.block.service;

import com.example.luvisluvproject.domain.block.dto.BlockRequestDto;
import com.example.luvisluvproject.domain.block.dto.BlockResponseDto;
import com.example.luvisluvproject.domain.block.entity.Block;
import com.example.luvisluvproject.domain.block.repository.BlockRepository;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.BlockErrorCode;
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
	public BlockResponseDto blockUser(Long blockerId, BlockRequestDto requestDto) {
		Member blocker = memberRepository.findById(blockerId)
			.orElseThrow(() -> new CustomRuntimeException(BlockErrorCode.MEMBER_NOT_FOUND));

		Member blocked = memberRepository.findById(requestDto.getBlockedId())
			.orElseThrow(() -> new CustomRuntimeException(BlockErrorCode.MEMBER_NOT_FOUND));

		if (blockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
			throw new CustomRuntimeException(BlockErrorCode.ALREADY_BLOCKED);
		}

		Block block = Block.builder()
			.blocker(blocker)
			.blocked(blocked)
			.build();

		blockRepository.save(block);

		return new BlockResponseDto("사용자를 차단했습니다.");
	}

	@Transactional
	public BlockResponseDto unblockUser(Long blockerId, Long blockedId) {
		Member blocker = memberRepository.findById(blockerId)
			.orElseThrow(() -> new CustomRuntimeException(BlockErrorCode.MEMBER_NOT_FOUND));

		Member blocked = memberRepository.findById(blockedId)
			.orElseThrow(() -> new CustomRuntimeException(BlockErrorCode.MEMBER_NOT_FOUND));

		Block block = blockRepository.findByBlockerAndBlocked(blocker, blocked)
			.orElseThrow(() -> new CustomRuntimeException(BlockErrorCode.BLOCK_NOT_FOUND));

		blockRepository.delete(block);

		return new BlockResponseDto("차단을 해제했습니다.");
	}

	public List<BlockResponseDto> getBlockedUsers(Long blockerId) {
		Member blocker = memberRepository.findById(blockerId)
			.orElseThrow(() -> new CustomRuntimeException(BlockErrorCode.MEMBER_NOT_FOUND));

		return blockRepository.findAllByBlocker(blocker).stream()
			.map(block -> new BlockResponseDto(block.getBlocked().getUsername() + "을(를) 차단 중입니다."))
			.collect(Collectors.toList());
	}
}