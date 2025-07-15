package com.example.luvisluvproject.domain.membermatchinfo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.match.dto.ResponseMatchMemberDto;
import com.example.luvisluvproject.domain.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberMatchInfoHandler {

	private final MemberMatchInfoRepository memberMatchInfoRepository;

	@Transactional
	public void createMMI(Member me, List<ResponseMatchMemberDto> distinctMatchMemberDtoList) {

		Long meId = me.getId();
		Set<Long> ids = distinctMatchMemberDtoList.stream().map(ResponseMatchMemberDto::getId).collect(Collectors.toSet());
		List<MemberMatchInfo> existing = memberMatchInfoRepository.findAllByMeIdAndTargetIdIn(meId, ids);

		System.out.println("existing = " + existing);
		Map<Long, MemberMatchInfo> existingMap = existing.stream()
			.collect(Collectors.toMap(MemberMatchInfo::getTargetId, Function.identity()));
		
		//저장할 구조
		Set<MemberMatchInfo> savedMMI = new HashSet<>();

		for(Long targetId : ids) {
			if(existingMap.containsKey(targetId)) {
				MemberMatchInfo matchInfo = existingMap.get(targetId);
				matchInfo.plusScore();
				savedMMI.add(matchInfo);
			} else {
				MemberMatchInfo memberMatchInfo = new MemberMatchInfo(
					meId, targetId, false, false, LocalDateTime.now());
				memberMatchInfo.plusScore();
				savedMMI.add(memberMatchInfo);
			}
		}
		
		memberMatchInfoRepository.saveAll(savedMMI);
	}
}
