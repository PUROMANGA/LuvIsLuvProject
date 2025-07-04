package com.example.luvisluvproject.domain.memberInteractionLog.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberTagLikeCount;

public interface MemberTagLikeCountRepository extends JpaRepository<MemberTagLikeCount, Long> {
	List<MemberTagLikeCount> findAllByMemberId(Long memberId);
	@Query(value = "select * "
		+ "from member_tag_like_counts m "
		+ "where m.member_id = :memberId "
		+ "and DATE(m.creat_time) = :date", nativeQuery = true)
	List<MemberTagLikeCount> findAllByMemberIdAndCreatTime(Long memberId, LocalDate date);
}
