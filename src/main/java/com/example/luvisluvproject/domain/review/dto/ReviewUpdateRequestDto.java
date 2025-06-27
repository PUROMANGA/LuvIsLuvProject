package com.example.luvisluvproject.domain.review.dto;

import lombok.Getter;

@Getter
public class ReviewUpdateRequestDto {

	private final String content;

	public ReviewUpdateRequestDto(String content) {
		this.content = content;
	}
}
