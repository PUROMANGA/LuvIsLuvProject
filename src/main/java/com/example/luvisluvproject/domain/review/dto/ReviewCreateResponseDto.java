package com.example.luvisluvproject.domain.review.dto;

import java.time.LocalDateTime;

import com.example.luvisluvproject.domain.review.entity.Review;

import lombok.Getter;

@Getter
public class ReviewCreateResponseDto {

	private final Long reviewId;
	private final Long storeId;
	private final Long memberId;
	private final int rating;
	private final String content;
	private final LocalDateTime createdAt;

	public ReviewCreateResponseDto(Review review) {
		this.reviewId = review.getId();
		this.storeId = review.getStore().getId();
		this.memberId = review.getMember().getId();
		this.rating = review.getRating();
		this.content = review.getContent();
		this.createdAt = review.getCreatTime();
	}
}
