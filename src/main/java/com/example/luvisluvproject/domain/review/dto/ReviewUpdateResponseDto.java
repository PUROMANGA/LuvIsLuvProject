package com.example.luvisluvproject.domain.review.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ReviewUpdateResponseDto {

	private final Long reviewId;
	private final LocalDateTime updatedAt;
	private final String message;

	public ReviewUpdateResponseDto(Long reviewId, LocalDateTime updatedAt, String message) {
		this.reviewId = reviewId;
		this.updatedAt = updatedAt;
		this.message = message;
	}
}
