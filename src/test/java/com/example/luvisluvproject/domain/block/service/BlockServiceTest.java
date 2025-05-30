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
		blocker = Member.builder().id(1L).name("blocker").build();
		blocked = Member.builder().id(2L).name("blocked").build();
	}

	@Test
	void 차단_성공() {
		// given
		BlockRequestDto requestDto = new BlockRequestDto(blocked.getId());

		given(memberRepository.findById(blocker.getId())).willReturn(Optional.of(blocker));
		given(memberRepository.findById(blocked.getId())).willReturn(Optional.of(blocked));
		given(blockRepository.existsByBlockerAndBlocked(blocker, blocked)).willReturn(false);

		// when
		BlockResponseDto response = blockService.blockUser(blocker.getId(), requestDto);

		// then
		assertThat(response.getMessage()).isEqualTo("사용자를 차단했습니다.");
		then(blockRepository).should().save(any(Block.class));
	}

	@Test
	void 자기자신을_차단하려고_하면_예외발생() {
		// given
		BlockRequestDto requestDto = new BlockRequestDto(blocker.getId());

		given(memberRepository.findById(blocker.getId())).willReturn(Optional.of(blocker));

		// when & then
		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () ->
			blockService.blockUser(blocker.getId(), requestDto));

		assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.CANNOT_BLOCK_SELF);
	}

	@Test
	void 이미_차단한_사용자일_경우_예외발생() {
		// given
		BlockRequestDto requestDto = new BlockRequestDto(blocked.getId());

		given(memberRepository.findById(blocker.getId())).willReturn(Optional.of(blocker));
		given(memberRepository.findById(blocked.getId())).willReturn(Optional.of(blocked));
		given(blockRepository.existsByBlockerAndBlocked(blocker, blocked)).willReturn(true);

		// when & then
		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () ->
			blockService.blockUser(blocker.getId(), requestDto));

		assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.ALREADY_BLOCKED);
	}

	@Test
	void 차단_해제_성공() {
		// given
		given(memberRepository.findById(blocker.getId())).willReturn(Optional.of(blocker));
		given(memberRepository.findById(blocked.getId())).willReturn(Optional.of(blocked));
		given(blockRepository.findByBlockerAndBlocked(blocker, blocked))
			.willReturn(Optional.of(Block.builder().blocker(blocker).blocked(blocked).build()));

		// when
		BlockResponseDto response = blockService.unblockUser(blocker.getId(), blocked.getId());

		// then
		assertThat(response.getMessage()).isEqualTo("차단을 해제했습니다.");
		then(blockRepository).should().delete(any(Block.class));
	}
}
