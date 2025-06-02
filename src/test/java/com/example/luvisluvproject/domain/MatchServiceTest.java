package com.example.luvisluvproject.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import com.example.luvisluvproject.domain.chat.event.ChatCreateEvent;
import com.example.luvisluvproject.domain.match.entity.MatchStatus;
import com.example.luvisluvproject.domain.match.service.MatchService;
import com.example.luvisluvproject.domain.match.dto.AcceptMatchDto;
import com.example.luvisluvproject.domain.match.dto.MatchResponseDto;
import com.example.luvisluvproject.domain.match.entity.Match;
import com.example.luvisluvproject.domain.match.repository.MatchRepository;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.enums.UserRole;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)

public class MatchServiceTest {
	@Mock
	private MatchRepository matchRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private RedisTemplate<String, Object> redisTemplate;
	@Mock
	private ApplicationEventPublisher applicationEventPublisher;
	@InjectMocks
	private MatchService matchService;

	Member senderMember = new Member(
		1L,
		"송진영",
		"songjinyong@email.com",
		"test1234",
		LocalDate.parse("2001-01-01"),
		UserRole.USER,
		true,
		1L
	);

	Member receiverMember = new Member(
		2L,
		"이유리",
		"leeyuuri@email.com",
		"test5678",
		LocalDate.parse("2001-02-01"),
		UserRole.USER,
		true,
		1L
	);

	Match match = new Match(
		1L,
		senderMember.getId(),
		receiverMember.getId()
	);

	SetOperations<String, Object> setOps;
	AcceptMatchDto acceptMatchDto = new AcceptMatchDto(MatchStatus.ACCEPTED);

	@Test
	@DisplayName("송진영이 이유리에게 매칭을 걸었습니다")
	public void createMatchSongToLee() {

		setOps = mock(SetOperations.class);
		given(redisTemplate.opsForSet()).willReturn(setOps);

		//given
		given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(senderMember));
		given(memberRepository.findById(anyLong())).willReturn(Optional.of(receiverMember));

		//when
		MatchResponseDto result = matchService.createMatchService(receiverMember.getId(), senderMember.getEmail());

		//then
		assertThat(result).isNotNull();
		String expectedKey = senderMember.getId() + ":" + receiverMember.getId();
		verify(setOps, times(1)).add(eq(expectedKey), any(Match.class));
	}

	@Test
	@DisplayName("이유리가 송진영의 매칭을 수락하였습니다.")
	public void patchMatchServiceTest() {

		setOps = mock(SetOperations.class);
		given(redisTemplate.opsForSet()).willReturn(setOps);

		Set<Object> matchSet = new HashSet<>();
		matchSet.add(match);

		//given
		given(matchRepository.findById(anyLong())).willReturn(Optional.of(match));
		given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(receiverMember));
		given(setOps.members(anyString())).willReturn(matchSet);
		Match newMatch = matchSet.stream().map(obj -> {
				Match m = (Match)obj;
				m.updateMatchingStatus(acceptMatchDto);
				return m;
			})
			.findFirst()
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MATCH_NOT_FOUND));

		given(matchRepository.save(newMatch)).willReturn(newMatch);

		//when
		MatchResponseDto result = matchService.patchMatchService(match.getId(), acceptMatchDto,
			receiverMember.getEmail());

		//then
		assertThat(result.getMatchStatus()).isEqualTo(MatchStatus.ACCEPTED);
		String expectedKey = senderMember.getId() + ":" + receiverMember.getId();
		verify(applicationEventPublisher, times(1)).publishEvent(any(ChatCreateEvent.class));
		verify(redisTemplate, times(1)).delete(eq(expectedKey));
	}

	@Test
	@DisplayName("해당 회원이 받은 매칭 전부를 slice로 가져옵니다.")
	public void getMatchServiceTest() {
		//given

		List<Match> matchList = new ArrayList<>();
		matchList.add(match);
		Pageable pageable = PageRequest.of(0,10);
		Slice<Match> matches = new SliceImpl<>(matchList, pageable, false);

		given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(receiverMember));
		given(matchRepository.findMatchByReceiverId(anyLong(), any(Pageable.class))).willReturn(matches);

		//when
		Slice<MatchResponseDto> matchResponseDtoSlice = matchService.getMatchService(receiverMember.getEmail(), pageable);

		//then
		assertThat(matchResponseDtoSlice).isNotNull();
	}
}
