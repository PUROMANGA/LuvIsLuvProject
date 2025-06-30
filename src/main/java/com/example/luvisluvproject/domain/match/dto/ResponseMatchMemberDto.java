package com.example.luvisluvproject.domain.match.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ResponseMatchMemberDto {
	private Long id;
	private String name;
	private String content;

	public ResponseMatchMemberDto(Long id, String name, String content) {
		this.id = id;
		this.name = name;
		this.content = content;
	}
}
