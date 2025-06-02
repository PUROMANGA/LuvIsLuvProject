package com.example.luvisluvproject.domain.block.entity;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Block extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 차단한 사용자
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blocker_id", nullable = false)
	private Member blocker;

	// 차단당한 사용자
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blocked_id", nullable = false)
	private Member blocked;

	// 차단 타입 (일반 차단, 신고 후 차단 등)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BlockType blockType;

	// 프로필 접근 차단
	@Builder.Default
	private boolean blockUserAccess = true;

	// 매칭 시스템 제외 여부
	@Builder.Default
	private boolean excludeFromMatching = true;
	// 매칭 필터링 시

	// 추천 시스템 제외 여부
	@Builder.Default
	private boolean excludeFromRecommendation = true;

	// 차단 해제 여부 (soft delete 개념)
	@Builder.Default
	private boolean unblocked = false;

	// 차단 해제 시간 기록
	private java.time.LocalDateTime unblockedAt;

	// 해제 처리 메서드
	public void unblock() {
		this.unblocked = true;
		this.unblockedAt = java.time.LocalDateTime.now();
	}

	public enum BlockType {
		MANUAL, // 사용자가 직접 차단
		AFTER_REPORT, // 신고 후 차단
		SYSTEM // 시스템 또는 관리자가 자동으로 차단
	}
}
