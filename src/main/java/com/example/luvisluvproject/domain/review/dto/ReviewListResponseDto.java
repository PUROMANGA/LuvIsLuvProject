package com.example.luvisluvproject.domain.review.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ReviewListResponseDto {

	private final Long reviewId;
	private final int rating;
	private final String content;
	private final LocalDateTime createdAt;

	public ReviewListResponseDto(Long reviewId, int rating, String content, LocalDateTime createdAt) {
		this.reviewId = reviewId;
		this.rating = rating;
		this.content = content;
		this.createdAt = createdAt;
	}
}
