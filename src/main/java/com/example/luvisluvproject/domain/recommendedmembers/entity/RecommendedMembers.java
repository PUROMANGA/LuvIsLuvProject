package com.example.luvisluvproject.domain.recommendedmembers.entity;

import com.example.luvisluvproject.domain.match.dto.ResponseMatchMemberDto;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.recommendedmembers.enums.RecommendedCategory;
import com.example.luvisluvproject.global.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "recommended_members")
public class RecommendedMembers extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;

	private Long memberId;

	private Long recommendedId;

	@Enumerated(EnumType.STRING)
	private RecommendedCategory recommendedCategory;

	private Long exposureScore;

	public RecommendedMembers(Member me, ResponseMatchMemberDto responseMatchMemberDto) {
		this.memberId = me.getId();
		this.recommendedId = responseMatchMemberDto.getId();
		this.recommendedCategory = RecommendedCategory.TAG;
		this.exposureScore = 0L;
	}

	public void plusExcposureScore() {
		this.exposureScore++;
	}
}
