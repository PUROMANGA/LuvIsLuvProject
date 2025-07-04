package com.example.luvisluvproject.domain.recommendedmembers.repsitory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luvisluvproject.domain.recommendedmembers.entity.RecommendedMembers;

public interface RecommendedMembersRepository extends JpaRepository<RecommendedMembers, Long> {
	boolean existsByMemberIdAndRecommendedId(Long memberId, Long recommendedId);

	Optional<RecommendedMembers> findByMemberIdAndRecommendedId(Long id, Long id1);
}
