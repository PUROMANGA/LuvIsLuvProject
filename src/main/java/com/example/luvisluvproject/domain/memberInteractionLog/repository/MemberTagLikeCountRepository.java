package com.example.luvisluvproject.domain.memberInteractionLog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberTagLikeCount;

public interface MemberTagLikeCountRepository extends JpaRepository<MemberTagLikeCount, Long> {
	List<MemberTagLikeCount> findAllByMemberId(Long memberId);
}
