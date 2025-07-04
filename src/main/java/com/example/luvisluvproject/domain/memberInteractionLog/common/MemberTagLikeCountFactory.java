package com.example.luvisluvproject.domain.memberInteractionLog.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberTagLikeCount;

@Component
public class MemberTagLikeCountFactory {

	public List<MemberTagLikeCount> createMemberTagLikeCount(Map<String, Double> savedTupleForTag) {

		List<MemberTagLikeCount> memberInteractionLogList = new ArrayList<>();

		for(Map.Entry<String, Double> entry : savedTupleForTag.entrySet()) {
			memberInteractionLogList.add(
				new MemberTagLikeCount(entry.getKey(), entry.getValue()));
		}

		return memberInteractionLogList;
	}
}
