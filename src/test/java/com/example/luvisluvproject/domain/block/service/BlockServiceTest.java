package com.example.luvisluvproject.domain.block.service;

import com.example.luvisluvproject.domain.block.dto.BlockRequestDto;
import com.example.luvisluvproject.domain.block.dto.BlockResponseDto;
import com.example.luvisluvproject.domain.block.entity.Block;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class BlockServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private BlockRepository blockRepository;

	@InjectMocks
	private BlockService blockService;

	private Member blocker;
	private Member blocked;

	@BeforeEach
	void setUp() {
		blocker = Member.builder().id(1L).name("blocker").build();
		blocked = Member.builder().id(2L).name("blocked").build();
	}

	@Test
	void 차단_성공() {
		// given
		BlockRequestDto requestDto = new BlockRequestDto(
			blocked.getId(), true, true, true, true, true, "MANUAL"
		);

		given(memberRepository.findById(blocker.getId())).willReturn(Optional.of(blocker));
		given(memberRepository.findById(blocked.getId())).willReturn(Optional.of(blocked));
		given(blockRepository.existsByBlockerAndBlocked(blocker, blocked)).willReturn(false);
		given(blockRepository.save(any(Block.class))).willAnswer(invocation -> {
			Block block = invocation.getArgument(0);
			return block;
		});

		// when
		BlockResponseDto response = blockService.blockUser(blocker.getId(), requestDto);

		// then
		assertThat(response.getMessage()).contains("차단");
		assertThat(response.getBlockedId()).isEqualTo(blocked.getId());
		assertThat(response.getBlockTime()).isNotNull();
	}

	@Test
	void 자기자신을_차단하려고_하면_예외발생() {
		// given
		BlockRequestDto requestDto = new BlockRequestDto(
			blocker.getId(), true, true, true, true, true, "MANUAL"
		);

		given(memberRepository.findById(blocker.getId())).willReturn(Optional.of(blocker));

		// when & then
		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () ->
			blockService.blockUser(blocker.getId(), requestDto));

		assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.CANNOT_BLOCK_SELF);
	}

	@Test
	void 차단_해제_정상작동() {
		// given
		Block block = Block.builder()
			.blocker(blocker)
			.blocked(blocked)
			.blockType(Block.BlockType.MANUAL)
			.build();

		given(memberRepository.findById(blocker.getId())).willReturn(Optional.of(blocker));
		given(memberRepository.findById(blocked.getId())).willReturn(Optional.of(blocked));
		given(blockRepository.findByBlockerAndBlocked(blocker, blocked)).willReturn(Optional.of(block));

		// when
		String response = blockService.unblockUser(blocker.getId(), blocked.getId());

		// then
		assertThat(response).contains("해제");
	}

	@Test
	void 차단_목록_조회_결과는_빈리스트() {
		// given
		given(memberRepository.findById(blocker.getId())).willReturn(Optional.of(blocker));
		given(blockRepository.findAllByBlocker(blocker)).willReturn(Collections.emptyList());

		// when
		List<String> results = blockService.getBlockedUsers(blocker.getId());

		// then
		assertThat(results).isEmpty();
	}
}
