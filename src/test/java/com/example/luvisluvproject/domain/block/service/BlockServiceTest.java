package com.example.luvisluvproject.domain.block.service;

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
		Long blockedId = blocked.getId();

		given(memberRepository.findById(blocker.getId())).willReturn(Optional.of(blocker));
		given(memberRepository.findById(blockedId)).willReturn(Optional.of(blocked));

		// when
		String response = blockService.blockUser(blocker.getId(), blockedId);

		// then
		assertThat(response).contains("차단");
	}

	@Test
	void 자기자신을_차단하려고_하면_예외발생() {
		// given
		Long blockedId = blocker.getId();

		given(memberRepository.findById(blocker.getId())).willReturn(Optional.of(blocker));

		// when & then
		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () ->
			blockService.blockUser(blocker.getId(), blockedId));

		assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.CANNOT_BLOCK_SELF);
	}

	@Test
	void 차단_해제_정상작동() {
		// given
		given(memberRepository.findById(blocker.getId())).willReturn(Optional.of(blocker));
		given(memberRepository.findById(blocked.getId())).willReturn(Optional.of(blocked));

		// when
		String response = blockService.unblockUser(blocker.getId(), blocked.getId());

		// then
		assertThat(response).contains("해제");
	}

	@Test
	void 차단_목록_조회_결과는_빈리스트() {
		// given
		given(memberRepository.findById(blocker.getId())).willReturn(Optional.of(blocker));

		// when
		List<String> results = blockService.getBlockedUsers(blocker.getId());

		// then
		assertThat(results).isEmpty();
	}
}
