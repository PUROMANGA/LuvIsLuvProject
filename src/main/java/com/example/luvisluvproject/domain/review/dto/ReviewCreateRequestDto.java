package com.example.luvisluvproject.domain.review.dto;

import lombok.Getter;

@Getter
public class ReviewCreateRequestDto {

	private final int rating;
	private final String content;

	public ReviewCreateRequestDto(int rating, String content) {
		this.rating = rating;
		this.content = content;
	}
}
