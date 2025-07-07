package com.example.luvisluvproject.admin.sanction.dto;

import java.time.LocalDateTime;

import com.example.luvisluvproject.admin.sanction.entity.Sanction;

import lombok.Getter;

@Getter
public class SanctionSuccessDto {

	private Long memberId;

	private Boolean isBanStatus;

	private LocalDateTime banExpireAt;

	public SanctionSuccessDto(Sanction sanction) {
		this.memberId = sanction.getMemberId();
		this.isBanStatus = sanction.getIsBanStatus();
		this.banExpireAt = sanction.getBanExpireAt();
	}
}
