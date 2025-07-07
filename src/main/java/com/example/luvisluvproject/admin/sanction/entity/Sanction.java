package com.example.luvisluvproject.admin.sanction.entity;

import java.time.LocalDateTime;

import com.example.luvisluvproject.admin.sanction.dto.SanctionRequestDto;
import com.example.luvisluvproject.global.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Sanction extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long memberId;

	private Boolean isBanStatus;

	private LocalDateTime banExpireAt;

	public Sanction(Long memberId, Boolean isBanStatus, SanctionRequestDto sanctionRequestDto) {
		this.memberId = memberId;
		this.isBanStatus = isBanStatus;
		this.banExpireAt = LocalDateTime.now().plusDays(sanctionRequestDto.getBanPeriodDays());
	}

	public void updateStatus() {
		this.isBanStatus = false;
	}
}
