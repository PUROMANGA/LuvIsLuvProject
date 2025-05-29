package com.example.luvisluvproject.domain.match.dto;

import lombok.Getter;

@Getter

public class AcceptMatchDto {
	private boolean isLike;

	public AcceptMatchDto(boolean isLike) {
		this.isLike = isLike;
	}
}
