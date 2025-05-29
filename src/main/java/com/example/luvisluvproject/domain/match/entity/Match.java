package com.example.luvisluvproject.domain.match.entity;

import com.example.luvisluvproject.global.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Table(name = "matches")
@Entity

public class Match extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long senderId;
	private Long receiverId;
	private boolean isLike;

	public Match(Long senderId, Long receiverId, boolean isLike) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.isLike = isLike;
	}
}
