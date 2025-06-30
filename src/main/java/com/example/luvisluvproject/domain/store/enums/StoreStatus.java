package com.example.luvisluvproject.domain.store.enums;

import org.springframework.data.redis.connection.stream.StreamInfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 가게의 영업 상태를 나타내는 열거형(Enum) 클래스
 *
 * - OPEN: 가게가 영업 중인 상태
 * - CLOSED: 가게가 일시적으로 문을 닫은 상태
 * - TERMINATED: 가게가 영구적으로 폐업된 상태
 */
@RequiredArgsConstructor
@Getter

public enum StoreStatus {
	OPEN("영업중"),
	CLOSED("영업 종료"),
	TERMINATED("폐업");

	private final String koreanName;

}