package com.example.luvisluvproject.domain.match.entity;

import com.example.luvisluvproject.domain.match.dto.AcceptMatchDto;
import com.example.luvisluvproject.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "matches")
@Entity
@NoArgsConstructor

public class Match extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private Long senderId;
	@Column(nullable = false)
	private Long receiverId;
	@Column(nullable = false)
	private boolean isLike;

	@Enumerated(EnumType.STRING)
	private MatchStatus matchStatus;

	/**
	 * 매칭상태 거절로 변경
	 */

	public void setRejectedMatching() {
		this.matchStatus = MatchStatus.REJECTED;
	}

	/**
	 * 매칭상태 성공으로 변경
	 */

	public void setAcceptedMatching() {
		this.matchStatus = MatchStatus.ACCEPTED;
	}

	/**
	 *
	 * @param acceptMatchDto
	 */

	public void updateMatchStatus(AcceptMatchDto acceptMatchDto) {
		this.isLike = acceptMatchDto.isLike();
	}

	public Match(Long senderId, Long receiverId) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.isLike = false;
	}

	public Match(Long senderId, Long receiverId, boolean isLike) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.isLike = isLike;
	}

	public Match(Long id, Long senderId, Long receiverId, boolean isLike) {
		this.id = id;
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.isLike = isLike;
	}
}
