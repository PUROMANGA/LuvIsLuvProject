package com.example.luvisluvproject.domain.match.Service;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.luvisluvproject.domain.match.dto.AcceptMatchDto;
import com.example.luvisluvproject.domain.match.dto.MatchRequestDto;
import com.example.luvisluvproject.domain.match.dto.MatchResponseDto;
import com.example.luvisluvproject.domain.match.entity.Match;
import com.example.luvisluvproject.domain.match.repository.MatchRepository;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class MatchService {

	private final MatchRepository matchRepository;
	private final MemberRepository memberRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * 매칭을 걸면 해당 사람에게 요청이 갑니다.
	 * @param matchRequestDto
	 * @param email
	 * @return
	 */
	@SuppressWarnings("checkstyle:RegexpMultiline")
	public MatchResponseDto createMatchService(MatchRequestDto matchRequestDto, String email) {
		//senderId(로그인된 유저)
		Member senderMember = memberRepository.findByEmail(email).orElseThrow(() -> new CustomRuntimeException(
			ExceptionCode.USER_CANT_FIND));
		//receiverId(좋아요 받은 유저)
		Member receiverMember = memberRepository.findById(matchRequestDto.getReceiverId()).orElseThrow(() -> new CustomRuntimeException(
			ExceptionCode.USER_CANT_FIND));
		//match(수락 안 된 상태) 객체 생성
		Match match = new Match(senderMember.getId(), receiverMember.getId());
		//match를 저장합니다.
		String redisKey = senderMember.getId() + ":" + receiverMember.getId();
		redisTemplate.opsForSet().add(redisKey, match);
		return new MatchResponseDto(match);
	}

	/**
	 * 해당 매칭이 오고, 매칭의 상태를 '받음'으로 변경합니다.
	 * @param senderId
	 * @param email
	 * @retunr
	 */
	public MatchResponseDto patchMatchService(Long senderId, AcceptMatchDto acceptMatchDto, String email) {
		Member member = memberRepository.findByEmail(email).orElseThrow(() -> new CustomRuntimeException(
			ExceptionCode.USER_CANT_FIND));
		Set<Object> match = redisTemplate.opsForSet().members(senderId + ":" + member.getId());
		//매칭 찾고, 매핑하면서 상태변경
		Match savedMatch = matchRepository.save(match.stream().map(obj -> {Match m = (Match) obj;
			m.updateMatchStatus(acceptMatchDto);
			return m;})
			.findFirst()
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MATCH_NOT_FOUND)));
		//캐싱 삭제
		redisTemplate.delete(senderId + ":" + member.getId());
		//매칭 저장
		return new MatchResponseDto(savedMatch);
	}
}
