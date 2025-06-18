package com.example.luvisluvproject.domain.review.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ReviewListResponseDto {

	private final Long reviewId;
	private final int rating;
	private final String content;
	private final LocalDateTime creatAt;

	public ReviewListResponseDto(Long reviewId, int rating, String content, LocalDateTime creatAt) {
		this.reviewId = reviewId;
		this.rating = rating;
		this.content = content;
		this.creatAt = creatAt;
	}
}
