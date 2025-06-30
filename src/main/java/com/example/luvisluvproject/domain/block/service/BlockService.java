package com.example.luvisluvproject.domain.block.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.block.dto.BlockResponseDto;
import com.example.luvisluvproject.domain.block.dto.BlockUserDto;
import com.example.luvisluvproject.domain.block.entity.Block;
import com.example.luvisluvproject.domain.block.repository.BlockRepository;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlockService {

	private final BlockRepository blockRepository;
	private final MemberRepository memberRepository;

	/**
	 * 사용자 차단
	 */
	@Transactional
	public BlockResponseDto blockUser(String email, Long userId) {

		Member blocker = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		if (blocker.getId().equals(userId)) {
			throw new CustomRuntimeException(ExceptionCode.CANNOT_BLOCK_SELF);
		}

		Member blocked = memberRepository.findById(userId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		if (blockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
			throw new CustomRuntimeException(ExceptionCode.ALREADY_BLOCKED);
		}

		Block block = new Block(blocker, blocked);
		blockRepository.save(block);
		return new BlockResponseDto(block);
	}

	/**
	 * 사용자 차단 해제
	 */
	@Transactional
	public void unblockUser(String email, Long userId) {
		Member blocker = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		Member blocked = memberRepository.findById(userId)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		Block block = blockRepository.findByBlockerAndBlocked(blocker, blocked)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.BLOCK_NOT_FOUND));

		blockRepository.delete(block);
	}

	/**
	 * 차단한 사용자 목록 조회
	 */
	@Transactional(readOnly = true)
	public Slice<BlockUserDto> getBlockedUsers(String email, Pageable pageable) {
		Member blocker = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));

		Slice<BlockUserDto> blockUserDtos = blockRepository.findAllByBlocker(blocker, pageable).map(BlockUserDto::new);

		if (blockUserDtos.isEmpty()) {
			throw new CustomRuntimeException(ExceptionCode.BLOCK_NOT_FOUND);
		}

		return blockUserDtos;
	}
}
