package com.example.luvisluvproject.domain.memberInteractionLog.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberInteractionLog;

public interface MemberInteractionLogRepository extends JpaRepository<MemberInteractionLog, Long> {

	@Query(value = "select * "
		+ "from member_interaction_logs m "
		+ "where m.member_id = :id "
		+ "And Date(m.creat_time) = :date", nativeQuery = true)
	MemberInteractionLog findByMemberIdAndCreatTime(Long id, LocalDate date);
}
