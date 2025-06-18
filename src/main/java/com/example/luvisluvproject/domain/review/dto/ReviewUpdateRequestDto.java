package com.example.luvisluvproject.domain.review.dto;

import lombok.Getter;

@Getter
public class ReviewUpdateRequestDto {

	private final int rating;
	private final String content;

	public ReviewUpdateRequestDto(int rating, String content) {
		this.rating = rating;
		this.content = content;
	}
}
