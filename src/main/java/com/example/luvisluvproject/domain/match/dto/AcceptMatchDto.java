package com.example.luvisluvproject.domain.match.dto;

import com.example.luvisluvproject.domain.match.entity.MatchStatus;

import lombok.Getter;

@Getter

public class AcceptMatchDto {

	private final MatchStatus matchStatus;

	public AcceptMatchDto(MatchStatus matchStatus) {
		this.matchStatus = matchStatus;
	}
}
