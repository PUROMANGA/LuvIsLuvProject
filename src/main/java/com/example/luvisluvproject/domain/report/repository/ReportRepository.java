package com.example.luvisluvproject.domain.report.repository;

import com.example.luvisluvproject.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
	boolean existsByReporterIdAndReportedId(Long reporterId, Long reportedId);
}