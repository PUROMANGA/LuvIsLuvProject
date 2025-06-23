package com.example.luvisluvproject.domain.block.common;

import lombok.Getter;

@Getter

public enum BlockType {
	MANUAL,        // 사용자가 직접 차단
	AFTER_REPORT,  // 신고 후 자동 차단
	SYSTEM	// 시스템/관리자 차단
}
