package com.example.luvisluvproject.domain.block.service;

import com.example.luvisluvproject.domain.block.dto.BlockRequestDto;
import com.example.luvisluvproject.domain.block.dto.BlockResponseDto;
import com.example.luvisluvproject.domain.block.dto.BlockUserDto;
import com.example.luvisluvproject.domain.block.dto.UnblockResponseDto;
import com.example.luvisluvproject.domain.block.entity.Block;
import com.example.luvisluvproject.domain.block.entity.Block.BlockType;
import com.example.luvisluvproject.domain.block.repository.BlockRepository;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BlockServiceTest {

	@Mock
	private BlockRepository blockRepository;

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private BlockService blockService;

	private Member blocker;
	private Member blocked;

	@BeforeEach
	void setUp() {
		blocker = Member.builder().id(1L).name("BlockerUser").email("blocker@email.com").build();
		blocked = Member.builder().id(2L).name("BlockedUser").email("blocked@email.com").build();
	}

	@Test
	void 사용자를_차단한다() {
		// given
		BlockRequestDto requestDto = new BlockRequestDto(2L, true, true, BlockType.MANUAL);

		given(memberRepository.findById(1L)).willReturn(Optional.of(blocker));
		given(memberRepository.findById(2L)).willReturn(Optional.of(blocked));
		given(blockRepository.existsByBlockerAndBlocked(blocker, blocked)).willReturn(false);

		Block block = Block.builder()
			.blocker(blocker)
			.blocked(blocked)
			.blockUserAccess(true)
			.excludeFromMatching(true)
			.blockType(BlockType.MANUAL)
			.build();

		given(blockRepository.save(any())).willReturn(block);

		// when
		BlockResponseDto response = blockService.blockUser(1L, requestDto);

		// then
		assertThat(response.getBlockedId()).isEqualTo(2L);
		assertThat(response.getMessage()).isEqualTo("사용자를 차단했습니다.");
	}

	@Test
	void 자기_자신을_차단하면_예외_발생() {
		// given
		BlockRequestDto requestDto = new BlockRequestDto(1L, true, true, BlockType.MANUAL);

		// when & then
		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class,
			() -> blockService.blockUser(1L, requestDto));
		assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.CANNOT_BLOCK_SELF);
	}

	@Test
	void 이미_차단한_사용자를_다시_차단하면_예외_발생() {
		// given
		BlockRequestDto requestDto = new BlockRequestDto(2L, true, true, BlockType.MANUAL);

		given(memberRepository.findById(1L)).willReturn(Optional.of(blocker));
		given(memberRepository.findById(2L)).willReturn(Optional.of(blocked));
		given(blockRepository.existsByBlockerAndBlocked(blocker, blocked)).willReturn(true);

		// when & then
		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class,
			() -> blockService.blockUser(1L, requestDto));
		assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.ALREADY_BLOCKED);
	}

	@Test
	void 차단한_사용자를_차단해제한다() {
		// given
		Block block = Block.builder()
			.blocker(blocker)
			.blocked(blocked)
			.blockType(BlockType.MANUAL)
			.build();

		given(memberRepository.findById(1L)).willReturn(Optional.of(blocker));
		given(memberRepository.findById(2L)).willReturn(Optional.of(blocked));
		given(blockRepository.findByBlockerAndBlocked(blocker, blocked)).willReturn(Optional.of(block));

		// when
		UnblockResponseDto response = blockService.unblockUser(1L, 2L);

		// then
		assertThat(response.getUnblockedUserId()).isEqualTo(2L);
		assertThat(response.getMessage()).isEqualTo("차단을 해제했습니다.");
		assertThat(block.isUnblocked()).isTrue();
		assertThat(block.getUnblockedAt()).isNotNull();
	}

	@Test
	void 차단_목록을_조회한다() {
		// given
		Member blocked2 = Member.builder().id(3L).name("Blocked2").email("b2@email.com").build();
		Block block1 = Block.builder().blocker(blocker).blocked(blocked).build();
		Block block2 = Block.builder().blocker(blocker).blocked(blocked2).build();

		given(memberRepository.findById(1L)).willReturn(Optional.of(blocker));
		given(blockRepository.findAllByBlocker(blocker)).willReturn(List.of(block1, block2));

		// when
		List<BlockUserDto> result = blockService.getBlockedUsers(1L);

		// then
		assertThat(result).hasSize(2);
		assertThat(result).extracting("id").containsExactlyInAnyOrder(2L, 3L);
		assertThat(result).extracting("name").contains("BlockedUser", "Blocked2");
	}
}
