package com.example.luvisluvproject.domain.memberInteractionLog.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public enum MemberInteractionLogFiledType {
	GET_MATCH_COUNT("GetMatchCount"),
	MATCH_RECEIVED_COUNT("MatchReceivedCount"),
	MATCH_REQUEST_COUNT("MatchRequestCount"),
	MESSAGE_COUNT("MessageCount");

	private final String key;

	MemberInteractionLogFiledType(String key) {
		this.key = key;
	}

	private static final Map<String, MemberInteractionLogFiledType> map = Arrays.stream(values())
		.collect(Collectors.toMap(MemberInteractionLogFiledType::getKey, Function.identity()));

	public static Optional<MemberInteractionLogFiledType> fromKey(String key) {
		return Optional.ofNullable(map.get(key));
	}
}
