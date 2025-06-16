package com.example.luvisluvproject.domain.store.enums;

/**
 * 가게의 업종 유형을 나타내는 열거형(Enum) 클래스
 *
 * - BAR: 술집
 * - CAFE: 카페
 * - RESTAURANT: 식당
 * - SHOP: 잡화점, 소매점 등
 */

// table 빼고 관리 api 새로 만들기 flyway
public enum StoreType {
	BAR,         // 술집
	CAFE,        // 카페
	RESTAURANT,  // 음식점
	SHOP         // 일반 상점
}