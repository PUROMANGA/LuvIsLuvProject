package com.example.luvisluvproject.domain.block.entity;

import com.example.luvisluvproject.domain.block.enums.BlockType;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "blocks", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"blocker_id", "blocked_id"})
})
public class Block extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 차단한 사람
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blocker_id", nullable = false)
	private Member blocker;

	// 차단당한 사람
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blocked_id", nullable = false)
	private Member blocked;

	// 차단 유형 (ex. 악의적, 불쾌감 등)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private BlockType blockType;

	// 프로필 접근 차단 여부
	@Column(nullable = false)
	private boolean blockUserAccess;

	// 매칭 제외 여부
	@Column(nullable = false)
	private boolean excludeFromMatching;
	}
