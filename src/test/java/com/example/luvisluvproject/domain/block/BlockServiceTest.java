package com.example.luvisluvproject.domain.block;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.luvisluvproject.domain.block.dto.BlockResponseDto;
import com.example.luvisluvproject.domain.block.entity.Block;
import com.example.luvisluvproject.domain.block.repository.BlockRepository;
import com.example.luvisluvproject.domain.block.service.BlockService;
import com.example.luvisluvproject.global.common.TestFactory;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;

@ExtendWith(MockitoExtension.class)
public class BlockServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private BlockRepository blockRepository;

	@Spy
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Spy
	private TestFactory testFactory = new TestFactory(passwordEncoder);

	@InjectMocks
	private BlockService blockService;

	private Member blocker;
	private Member blocked;
	private Block block;

	@BeforeEach
	void setUp() {
		blocker = testFactory.loginTestOf("name",  "park1@email.com", "Test1234!");
		blocked = testFactory.loginTestOf("name",  "kim1@email.com", "Test1234!");
		block = testFactory.blockTestOf(blocker, blocked);

		testFactory.forTestUpdateId(blocked, 1L);
		testFactory.forTestUpdateId(blocker, 2L);
	}

	@Nested
	@DisplayName("블록 테스트")
	class BlockTest {

		@Test
		@DisplayName("사용자 차단 등록 실패")
		void blockUserFailureTest() {

			//given
			given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(blocker));
			given(memberRepository.findById(anyLong())).willReturn(Optional.of(blocked));
			given(blockRepository.existsByBlockerAndBlocked(blocker, blocked)).willReturn(true);

			//when
			CustomRuntimeException customRuntimeException = assertThrows(CustomRuntimeException.class, () -> blockService.blockUser(blocker.getEmail(), blocked.getId()));

			//then
			assertEquals("이미 차단한 사용자입니다.", customRuntimeException.getMessage());
		}

		@Test
		@DisplayName("사용자 차단 등록 성공")
		void blockUserSuccessTest() {

			//given
			given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(blocker));
			given(memberRepository.findById(anyLong())).willReturn(Optional.of(blocked));
			given(blockRepository.existsByBlockerAndBlocked(blocker, blocked)).willReturn(false);

			//when
			BlockResponseDto blockResponseDto = blockService.blockUser(blocker.getEmail(), blocked.getId());
			//then
			assertThat(blockResponseDto.getBlockedId()).isEqualTo(1L);
		}

		/**
		 * 성공했을 때는 아무것도 반환하지 않고 삭제만 하기 때문에 성공 테스트의 사례는 성공으로 간주합니다.
		 */
		@Test
		@DisplayName("사용자 차단 해제 실패")
		void unblockUserFailureTest() {
			//given
			given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(blocker));
			given(memberRepository.findById(anyLong())).willReturn(Optional.of(blocked));
			given(blockRepository.findByBlockerAndBlocked(blocker, blocked)).willReturn(Optional.empty());

			CustomRuntimeException customRuntimeException = assertThrows(CustomRuntimeException.class, () -> blockService.unblockUser(blocker.getEmail(), blocked.getId()));

			assertEquals("차단 정보가 존재하지 않습니다.", customRuntimeException.getMessage());
		}

		@Test
		@DisplayName("차단 목록 불러오기 실패")
		void getBlockedUsersFailureTest() {

			//given
			List<Block> blockList = new ArrayList<>();
			Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createTime"));
			Slice<Block> slice = new SliceImpl<>(blockList, pageable, false);

			given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(blocker));
			given(blockRepository.findAllByBlocker(blocker, pageable)).willReturn(slice);

			CustomRuntimeException customRuntimeException = assertThrows(CustomRuntimeException.class, () -> blockService.getBlockedUsers(blocker.getEmail(), pageable));

			assertEquals("차단 정보가 존재하지 않습니다.", customRuntimeException.getMessage());
		}


	}
}
