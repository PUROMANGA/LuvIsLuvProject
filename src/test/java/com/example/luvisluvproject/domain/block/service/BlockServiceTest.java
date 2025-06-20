package com.example.luvisluvproject.domain.block.service;

import com.example.luvisluvproject.domain.block.dto.BlockRequestDto;
import com.example.luvisluvproject.domain.block.dto.BlockResponseDto;
import com.example.luvisluvproject.domain.block.dto.UnblockResponseDto;
import com.example.luvisluvproject.domain.block.entity.Block;
import com.example.luvisluvproject.domain.block.enums.BlockType;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
		BlockRequestDto requestDto = new BlockRequestDto("blocked@email.com", true, true, BlockType.MANUAL);

		given(memberRepository.findByEmail("blocker@email.com")).willReturn(Optional.of(blocker));
		given(memberRepository.findByEmail("blocked@email.com")).willReturn(Optional.of(blocked));
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
		BlockResponseDto response = blockService.blockUser("blocker@email.com", requestDto);

		// then
		assertThat(response.getBlockedId()).isEqualTo(2L);
	}

	@Test
	void 자기_자신을_차단하면_예외_발생() {
		// given
		BlockRequestDto requestDto = new BlockRequestDto("blocker@email.com", true, true, BlockType.MANUAL);
		given(memberRepository.findByEmail("blocker@email.com")).willReturn(Optional.of(blocker));

		// when & then
		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class,
			() -> blockService.blockUser("blocker@email.com", requestDto));
		assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.CANNOT_BLOCK_SELF);
	}

	@Test
	void 이미_차단한_사용자를_다시_차단하면_예외_발생() {
		// given
		BlockRequestDto requestDto = new BlockRequestDto("blocked@email.com", true, true, BlockType.MANUAL);

		given(memberRepository.findByEmail("blocker@email.com")).willReturn(Optional.of(blocker));
		given(memberRepository.findByEmail("blocked@email.com")).willReturn(Optional.of(blocked));
		given(blockRepository.existsByBlockerAndBlocked(blocker, blocked)).willReturn(true);

		// when & then
		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class,
			() -> blockService.blockUser("blocker@email.com", requestDto));
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

		given(memberRepository.findByEmail("blocker@email.com")).willReturn(Optional.of(blocker));
		given(memberRepository.findByEmail("blocked@email.com")).willReturn(Optional.of(blocked));
		given(blockRepository.findByBlockerAndBlocked(blocker, blocked)).willReturn(Optional.of(block));

		// when
		UnblockResponseDto response = blockService.unblockUser("blocker@email.com", "blocked@email.com");

		// then
		assertThat(response.getUnblockedUserId()).isEqualTo(2L);
	}

	@Test
	void 차단_목록을_조회한다() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		Member blocked2 = Member.builder().id(3L).name("Blocked2").email("b2@email.com").build();
		Block block1 = Block.builder().blocker(blocker).blocked(blocked).build();
		Block block2 = Block.builder().blocker(blocker).blocked(blocked2).build();

		given(memberRepository.findByEmail("blocker@email.com")).willReturn(Optional.of(blocker));
		given(blockRepository.findAllByBlocker(blocker, pageable))
			.willReturn(new SliceImpl<>(List.of(block1, block2), pageable, false));

		// when
		var result = blockService.getBlockedUsersByEmail("blocker@email.com", pageable);

		// then
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent()).extracting("email")
			.containsExactlyInAnyOrder("blocked@email.com", "b2@email.com");
	}
}
