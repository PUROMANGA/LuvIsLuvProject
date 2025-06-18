package com.example.luvisluvproject.domain.report.repository;

import com.example.luvisluvproject.domain.report.entity.Report;
import com.example.luvisluvproject.domain.report.entity.ReportTargetType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ReportRepository
 * 신고 정보를 데이터베이스에서 관리하는 JPA 리포지토리입니다.
 */
public interface ReportRepository extends JpaRepository<Report, Long> {

	/**
	 * 동일한 대상에 대해 동일 사용자가 이미 신고했는지 확인합니다.
	 *
	 * @param reporterId 신고자 ID
	 * @param targetId   신고 대상 ID
	 * @param targetType 신고 대상 타입 (USER, MESSAGE)
	 * @return 이미 신고했으면 true
	 */
	boolean existsByReporterIdAndTargetIdAndTargetType(Long reporterId, Long targetId, ReportTargetType targetType);
}
