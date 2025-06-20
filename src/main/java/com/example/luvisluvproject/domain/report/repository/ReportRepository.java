package com.example.luvisluvproject.domain.report.repository;

import com.example.luvisluvproject.domain.report.entity.Report;
import com.example.luvisluvproject.domain.report.entity.ReportTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * ReportRepository
 * 신고 정보를 데이터베이스에서 관리하는 JPA 리포지토리입니다.
 */
public interface ReportRepository extends JpaRepository<Report, Long> {

	/**
	 * 동일한 대상에 대해 동일 사용자가 이미 신고했는지 확인합니다.
	 * 이메일 기준으로 중복 신고 여부를 판단합니다.
	 *
	 * @param email        신고자의 이메일
	 * @param targetEmail  신고 대상자의 이메일
	 * @param targetType   신고 대상 타입
	 * @return true → 이미 신고함
	 */
	@Query("SELECT COUNT(r) > 0 FROM Report r WHERE r.reporter.email = :email AND r.targetEmail = :targetEmail AND r.targetType = :targetType")
	boolean existsByReporterEmailAndTargetEmailAndTargetType(
		@Param("email") String email,
		@Param("targetEmail") String targetEmail,
		@Param("targetType") ReportTargetType targetType
	);
}
