package com.example.luvisluvproject.domain.membermatchinfo;

import java.time.LocalDateTime;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class MemberMatchInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long meId;
	private Long targetId;

	private Boolean isBlocked;
	private Boolean isMatched;
	private LocalDateTime recommendedTime;
	private Long recommendedExposureScore = 0L;

	public MemberMatchInfo(Long meId, Long targetId, Boolean isBlocked, Boolean isMatched, LocalDateTime recommendedTime) {
		this.meId = meId;
		this.targetId = targetId;
		this.isBlocked = isBlocked;
		this.isMatched = isMatched;
		this.recommendedTime = recommendedTime;
	}

	public void plusScore() {
		if (this.recommendedExposureScore == null) {
			this.recommendedExposureScore = 0L;
		}

		if (recommendedExposureScore >= 3L) {
			return;
		}

		this.recommendedExposureScore += 1;
	}
}
