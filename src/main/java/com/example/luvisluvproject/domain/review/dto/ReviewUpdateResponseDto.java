package com.example.luvisluvproject.domain.review.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ReviewUpdateResponseDto {

	private final Long reviewId;
	private final LocalDateTime updatedAt;

	public ReviewUpdateResponseDto(Long reviewId, LocalDateTime updatedAt) {
		this.reviewId = reviewId;
		this.updatedAt = updatedAt;
	}
}
