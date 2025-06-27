package com.example.luvisluvproject.domain.block.entity;

import com.example.luvisluvproject.domain.block.common.BlockType;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Block
 * 사용자 간 차단 관계를 나타내는 엔티티
 * blocker(차단자) → blocked(차단된 사용자)
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "blocks")
public class Block extends BaseEntity {

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

	public Block(Member blocker, Member blocked) {
		this.blocker = blocker;
		this.blocked = blocked;
	}
}
