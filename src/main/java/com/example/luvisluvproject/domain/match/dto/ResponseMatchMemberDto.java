package com.example.luvisluvproject.domain.match.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ResponseMatchMemberDto {
	private Long id;
	private String name;

	public ResponseMatchMemberDto(Long id, String name) {
		this.id = id;
		this.name = name;
	}
}
