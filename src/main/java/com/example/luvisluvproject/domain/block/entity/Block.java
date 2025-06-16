package com.example.luvisluvproject.domain.block.entity;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Block
 * 사용자 간 차단 관계를 나타내는 엔티티
 * blocker(차단자) → blocked(차단된 사용자)
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Block extends BaseEntity {

	private boolean excludeFromMatching;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 차단을 수행한 사용자
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blocker_id", nullable = false)
	private Member blocker;

	/**
	 * 차단당한 사용자
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blocked_id", nullable = false)
	private Member blocked;

	/**
	 * 차단 유형 (직접 차단, 신고 후 차단, 시스템 차단 등)
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BlockType blockType;

	/**
	 * 프로필 접근 차단 여부
	 */
	private boolean blockUserAccess;

	/**
	 * 차단 해제 여부 (Soft delete 방식)
	 */
	@Builder.Default
	private boolean unblocked = false;

	/**
	 * 차단 해제된 시각 (null이면 해제되지 않음)
	 */
	private LocalDateTime unblockedAt;

	/**
	 * 차단 해제 처리 메서드
	 * unblocked → true, 해제 시각 기록
	 */
	public void unblock() {
		this.unblocked = true;
		this.unblockedAt = LocalDateTime.now();
	}

	/**
	 * 차단 유형 enum
	 */
	public enum BlockType {
		MANUAL,        // 사용자가 직접 차단
		AFTER_REPORT,  // 신고 후 자동 차단
		SYSTEM         // 시스템/관리자 차단
	}
}
