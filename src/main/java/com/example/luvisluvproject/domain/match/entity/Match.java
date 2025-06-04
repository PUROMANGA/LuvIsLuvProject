package com.example.luvisluvproject.domain.match.entity;

import com.example.luvisluvproject.domain.match.dto.AcceptMatchDto;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.global.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

	@Enumerated(EnumType.STRING)
	private MatchStatus matchStatus;

	public void updateMatchingStatus(AcceptMatchDto acceptMatchDto) {
		this.matchStatus = acceptMatchDto.getMatchStatus();
	}

	public Match(Long meId, Long receiverId) {
		this.senderId = meId;
		this.receiverId = receiverId;
	}


	public Match(Long id, Long senderId, Long receiverId, MatchStatus matchStatus) {
		this.id = id;
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.matchStatus = matchStatus;
	}
}
