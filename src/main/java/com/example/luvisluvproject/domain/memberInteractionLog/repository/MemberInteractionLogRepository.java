package com.example.luvisluvproject.domain.memberInteractionLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberInteractionLog;

public interface MemberInteractionLogRepository extends JpaRepository<MemberInteractionLog, Long> {
}
