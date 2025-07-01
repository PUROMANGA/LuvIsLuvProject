package com.example.luvisluvproject.domain.memberInteractionLog.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.luvisluvproject.domain.memberInteractionLog.enums.MemberInteractionLogFiledType;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "MemberInteractionLogs")
public class MemberInteractionLog {

	@Id
	private Long memberId;

	//매칭을 돌린 횟수
	private Double getMatchCount;

	//수락한 횟수
	private Double matchReceivedCount;

	//신청한 횟수
	private Double matchRequestCount;

	//메세지를 보낸 횟수
	private Double messageCount;

	@OneToMany(mappedBy = "memberInteractionLog")
	private List<MemberTagLikeCount> memberTagLikeCountList = new ArrayList<>();

	public MemberInteractionLog(Long memberId, Map<String, Double> savedTuple) {
		this.memberId = memberId;
		for(Map.Entry<String, Double> entry : savedTuple.entrySet()) {
			MemberInteractionLogFiledType.fromKey(entry.getKey()).ifPresent(t -> {
				switch (t) {
					case GET_MATCH_COUNT -> this.getMatchCount = entry.getValue();
					case MATCH_RECEIVED_COUNT -> this.matchReceivedCount = entry.getValue();
					case MATCH_REQUEST_COUNT -> this.matchRequestCount = entry.getValue();
					case MESSAGE_COUNT -> this.messageCount = entry.getValue();
				}
			});
		}
	}
}
