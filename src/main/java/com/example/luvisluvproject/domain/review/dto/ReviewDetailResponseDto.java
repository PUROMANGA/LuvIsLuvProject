package com.example.luvisluvproject.domain.review.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ReviewDetailResponseDto {

	private final Long reviewId;
	private final Long storeId;
	private final int rating;
	private final String content;
	private final LocalDateTime createdAt;

	public ReviewDetailResponseDto(Long reviewId, Long storeId, int rating, String content, LocalDateTime createdAt) {
		this.reviewId = reviewId;
		this.storeId = storeId;
		this.rating = rating;
		this.content = content;
		this.createdAt = createdAt;
	}
}
