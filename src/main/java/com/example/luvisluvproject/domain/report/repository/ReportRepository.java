package com.example.luvisluvproject.domain.report.repository;

import com.example.luvisluvproject.domain.report.entity.Report;
import com.example.luvisluvproject.domain.report.entity.ReportTargetType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
	boolean existsByReporterIdAndTargetIdAndTargetType(Long reporterId, Long targetId, ReportTargetType targetType);
}
