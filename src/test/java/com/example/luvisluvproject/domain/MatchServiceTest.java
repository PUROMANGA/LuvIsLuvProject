package com.example.luvisluvproject.domain;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.luvisluvproject.domain.match.Service.MatchService;
import com.example.luvisluvproject.domain.match.dto.MatchRequestDto;
import com.example.luvisluvproject.domain.match.dto.MatchResponseDto;
import com.example.luvisluvproject.domain.match.entity.Match;
import com.example.luvisluvproject.domain.match.repository.MatchRepository;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.enums.UserRole;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)

public class MatchServiceTest {
	@Mock
	private MatchRepository matchRepository;
	@Mock
	private MemberRepository memberRepository;
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

	MatchRequestDto matchRequestDto = new MatchRequestDto(senderMember);

	@BeforeEach
	void setUp() {
		given(matchRepository.save(match)).willReturn(match);
	}

	@Test
	@DisplayName("송진영이 이유리에게 매칭을 걸었습니다")
	public void createMatchSongToLee() {
		//given
		given(memberRepository.findByEmail(anyString())).willReturn(senderMember);
		given(memberRepository.findById(anyLong())).willReturn(Optional.of(receiverMember));

		//when
		MatchResponseDto result = matchService.createMatchService(matchRequestDto, senderMember.getEmail());

		//then
		assertThat(result).isNotNull();
	}

	@Test
	@DisplayName("이유리가 송진영의 매칭을 수락하였습니다.")
	public void patchMatchServiceTest() {

		//given
		given(matchRepository.findById(anyLong())).willReturn(Optional.of(match));
		match.updateMatchStatus();

		//when
		MatchResponseDto result = matchService.patchMatchService(match.getId(), receiverMember.getEmail());

		//then
		assertThat(result.isLike()).isTrue();
	}



}
