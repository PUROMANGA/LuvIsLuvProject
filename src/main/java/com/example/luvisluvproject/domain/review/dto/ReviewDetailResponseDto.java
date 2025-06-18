package com.example.luvisluvproject.domain.review.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ReviewDetailResponseDto {

	private final Long reviewId;
	private final String storeName;
	private final double averageRating;
	private final int rating;
	private final String content;
	private final LocalDateTime createdAt;

	public ReviewDetailResponseDto(Long reviewId, String storeName, double averageRating, int rating, String content,
		LocalDateTime createdAt) {
		this.reviewId = reviewId;
		this.storeName = storeName;
		this.averageRating = averageRating;
		this.rating = rating;
		this.content = content;
		this.createdAt = createdAt;
	}
}
