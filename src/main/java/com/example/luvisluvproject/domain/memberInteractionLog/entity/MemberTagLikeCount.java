package com.example.luvisluvproject.domain.memberInteractionLog.entity;

import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class MemberTagLikeCount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long memberId;

	private String tagName;

	private Double tagCount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private MemberInteractionLog memberInteractionLog;

	public MemberTagLikeCount(Long memberId, Map<String, Double> savedTupleForTag) {
		this.memberId = memberId;
		for(Map.Entry<String, Double> entry : savedTupleForTag.entrySet()) {
			this.tagName = entry.getKey();
			this.tagCount = entry.getValue();
		}
	}
}
