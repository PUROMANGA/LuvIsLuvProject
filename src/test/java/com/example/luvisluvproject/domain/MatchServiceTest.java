package com.example.luvisluvproject.domain;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import com.example.luvisluvproject.domain.match.Service.MatchService;
import com.example.luvisluvproject.domain.match.dto.AcceptMatchDto;
import com.example.luvisluvproject.domain.match.dto.MatchRequestDto;
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

import java.util.HashSet;
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
	@InjectMocks
	private MatchService matchService;

	Member senderMember = new Member(
		1L,
		"송진영",
		"songjinyong@email.com",
		"test1234",
		"2001-01-01",
		UserRole.USER,
		true
	);

	Member receiverMember = new Member(
		2L,
		"이유리",
		"leeyuuri@email.com",
		"test5678",
		"2001-02-01",
		UserRole.USER,
		true
	);

	Match match = new Match(
		1L,
		senderMember.getId(),
		receiverMember.getId(),
		false
	);

	SetOperations<String, Object> setOps;

	@BeforeEach
	public void setUp() {
		setOps = mock(SetOperations.class);
		given(redisTemplate.opsForSet()).willReturn(setOps);
	}

	MatchRequestDto matchRequestDto = new MatchRequestDto(senderMember);
	AcceptMatchDto acceptMatchDto = new AcceptMatchDto(true);

	@Test
	@DisplayName("송진영이 이유리에게 매칭을 걸었습니다")
	public void createMatchSongToLee() {
		//given
		given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(senderMember));
		given(memberRepository.findById(anyLong())).willReturn(Optional.of(receiverMember));

		//when
		MatchResponseDto result = matchService.createMatchService(matchRequestDto, senderMember.getEmail());

		//then
		assertThat(result).isNotNull();
		String expectedKey = senderMember.getId() + ":" + receiverMember.getId();
		verify(setOps, times(1)).add(eq(expectedKey), any(Match.class));
	}

	@Test
	@DisplayName("이유리가 송진영의 매칭을 수락하였습니다.")
	public void patchMatchServiceTest() {

		Set<Object> matchSet = new HashSet<>();

		//given
		given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(receiverMember));
		given(setOps.members(anyString())).willReturn(matchSet);
		matchSet.add(match);
		Match newMatch = matchSet.stream().map(obj -> {Match m = (Match) obj;
		m.updateMatchStatus(acceptMatchDto);
		return m;})
			.findFirst()
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MATCH_NOT_FOUND));

		given(matchRepository.save(newMatch)).willReturn(newMatch);

		//when
		MatchResponseDto result = matchService.patchMatchService(match.getId(), acceptMatchDto, receiverMember.getEmail());

		//then
		assertThat(result.isLike()).isTrue();
		String expectedKey = senderMember.getId() + ":" + receiverMember.getId();
		verify(redisTemplate, times(1)).delete(eq(expectedKey));
	}
}
