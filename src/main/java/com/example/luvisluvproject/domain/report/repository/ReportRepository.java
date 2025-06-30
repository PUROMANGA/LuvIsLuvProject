package com.example.luvisluvproject.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luvisluvproject.domain.report.entity.Report;

/**
 * ReportRepository
 * 신고 정보를 데이터베이스에서 관리하는 JPA 리포지토리입니다.
 */
public interface ReportRepository extends JpaRepository<Report, Long> {
}
