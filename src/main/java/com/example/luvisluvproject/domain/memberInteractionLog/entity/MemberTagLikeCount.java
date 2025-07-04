package com.example.luvisluvproject.domain.memberInteractionLog.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.luvisluvproject.global.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "member_tag_like_counts")
public class MemberTagLikeCount extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String tagName;

	private Double tagCount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private MemberInteractionLog memberInteractionLog;

	public MemberTagLikeCount(String tagName, Double tagCount) {
		this.tagName = tagName;
		this.tagCount = tagCount;
	}
}
