package com.example.luvisluvproject.domain.block.enums;

/**
 * BlockType
 * 사용자의 차단 유형을 나타내는 enum
 * - 내부 enum이 아닌, 외부 enum 클래스로 분리하여 재사용성과 가독성 확보
 */
public enum BlockType {
	MANUAL,        // 사용자가 직접 차단
	AFTER_REPORT,  // 신고 후 자동 차단
	SYSTEM         // 시스템 또는 관리자 차단
}
