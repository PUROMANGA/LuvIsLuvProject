package com.example.luvisluvproject.domain.report.entity;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.global.common.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Report
 * 신고 엔티티로, 사용자가 다른 유저 또는 메시지를 신고한 기록을 저장합니다.
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reports")
public class Report extends BaseEntity {

	/**
	 * 신고 고유 ID (PK)
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 신고자 (Member 엔티티와 연관)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporter_id", nullable = false)
	private Member reporter;

	/**
	 * 신고 대상자 (Member 엔티티와 연관)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reported_id", nullable = false)
	private Member reported;

	/**
	 * 신고 사유 (욕설, 스팸 등)
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReportReason reason;

	/**
	 * 추가 설명 (선택적으로 작성되는 신고 내용)
	 */
	@Column(columnDefinition = "TEXT")
	private String description;

	public Report(Member reporter, Member reported, ReportReason reason, String description) {
		this.reporter = reporter;
		this.reported = reported;
		this.reason = reason;
		this.description = description;
	}
}
