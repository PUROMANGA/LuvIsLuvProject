package com.example.luvisluvproject.domain.recommendedmembers.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.match.dto.ResponseMatchMemberDto;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.recommendedmembers.entity.RecommendedMembers;
import com.example.luvisluvproject.domain.recommendedmembers.repsitory.RecommendedMembersRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RecommendedMembersHandler {

	private final RecommendedMembersRepository recommendedMembersRepository;

	@Transactional
	public void createRecommendedMembers(Member me, List<ResponseMatchMemberDto> matchMemberDtoList) {

		List<RecommendedMembers> recommendedMembersList = new ArrayList<>();
		for (ResponseMatchMemberDto responseMatchMemberDto : matchMemberDtoList) {

			Optional<RecommendedMembers> existing = recommendedMembersRepository
				.findByMemberIdAndRecommendedId(me.getId(), responseMatchMemberDto.getId());

			if(existing.isEmpty()) {
				RecommendedMembers recommendedMembers = new RecommendedMembers(me, responseMatchMemberDto);
				recommendedMembers.plusExcposureScore();
				recommendedMembersList.add(recommendedMembers);
			} else {
				existing.get().plusExcposureScore();
				recommendedMembersList.add(existing.get());
			}
		}
		recommendedMembersRepository.saveAll(recommendedMembersList);
	}
}
