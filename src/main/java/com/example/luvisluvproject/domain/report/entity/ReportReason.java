package com.example.luvisluvproject.domain.report.entity;

/**
 * ReportReason
 * 신고 사유를 Enum으로 정의한 값 목록입니다.
 */
public enum ReportReason {
	ABUSIVE_LANGUAGE,       // 욕설 및 비속어
	SEXUAL_CONTENT,         // 성적인 콘텐츠
	SPAM,                   // 스팸 또는 광고성 콘텐츠
	INAPPROPRIATE_BEHAVIOR, // 부적절한 행동
	OTHER                   // 기타
}
