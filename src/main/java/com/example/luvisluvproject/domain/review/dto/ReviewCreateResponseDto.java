package com.example.luvisluvproject.domain.review.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ReviewCreateResponseDto {

	private final Long reviewId;
	private final LocalDateTime createdAt;
	private final String message;

	public ReviewCreateResponseDto(Long reviewId, LocalDateTime createdAt, String message) {
		this.reviewId = reviewId;
		this.createdAt = createdAt;
		this.message = message;
	}
}
