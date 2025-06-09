package com.example.luvisluvproject.domain.review.entity;

import java.time.LocalDateTime;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.store.entity.Store;
import com.example.luvisluvproject.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "reviews")
@NoArgsConstructor
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 외래 키 매핑 (Store)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store storeId;

	// 외래 키 매핑 (Member)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member memberId;

	@Min(1)
	@Max(5)
	@Column(nullable = false)
	private int rating;

	@Column(nullable = false)
	private String content;

	public LocalDateTime getCreatedAt() {
		return super.getCreatTime();
	}

	public void updateContent(String content) {
		this.content = content;
	}

	public Review(Store storeId, Member memberId, int rating, String content) {
		this.storeId = storeId;
		this.memberId = memberId;
		this.rating = rating;
		this.content = content;
	}
}
