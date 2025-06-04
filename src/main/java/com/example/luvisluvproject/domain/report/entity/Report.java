package com.example.luvisluvproject.domain.report.entity;

import com.example.luvisluvproject.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Report {

	// 신고 고유 ID (PK)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 신고자 (회원 엔티티와 연관관계)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporter_id", nullable = false)
	private Member reporter;

	// 신고 대상의 타입 (예: USER, MESSAGE)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReportTargetType targetType;

	// 신고 대상의 ID (유저 ID or 메시지 ID 등)
	@Column(nullable = false)
	private Long targetId;

	// 신고 사유 (예: 욕설, 스팸 등)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReportReason reason;

	// 상세 설명 (선택적으로 입력하는 신고 사유 설명)
	@Column(columnDefinition = "TEXT")
	private String description;

	// 신고 생성 시간
	// 주의: @Builder 사용 시 초기값 무시됨. @Builder.Default 필요.
	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();
}
