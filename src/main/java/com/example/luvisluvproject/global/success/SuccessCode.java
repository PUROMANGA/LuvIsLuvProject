package com.example.luvisluvproject.global.success;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {
	//회원가입, 로그인
	SIGNUP_SUCCESS(HttpStatus.OK, "회원가입 완료"),
	LOGIN_SUCCESS(HttpStatus.OK, "로그인 성공"),
	LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃 성공"),

	//회원
	FIND_MEMBER_SUCCESS(HttpStatus.OK, "회원 조회 성공"),
	UPDATE_MEMBER_SUCCESS(HttpStatus.OK, "회원정보 수정 성공"),
	DELETE_MEMBER_SUCCESS(HttpStatus.OK, "회원 탈퇴 완료"),

	//리뷰
	CREATE_REVIEW_SUCCESS(HttpStatus.OK, "리뷰 작성 성공"),
	UPDATE_REVIEW_SUCCESS(HttpStatus.OK, "리뷰 수정 성공"),
	GET_REVIEW_SUCCESS(HttpStatus.OK, "리뷰 상세 조회 성공"),
	GET_ALL_REVIEWS_SUCCESS(HttpStatus.OK, "리뷰 전체 조회 성공"),
	DELETE_REVIEW_SUCCESS(HttpStatus.OK, "리뷰 삭제 성공"),

	//TAG
	CREATE_TAG_REQUEST_SUCCESS(HttpStatus.OK, "태그 임시 저장 요청 성공"),
	SEARCH_TAGS_SUCCESS(HttpStatus.OK, "태그 자동완성 검색 성공"),
	CREATE_TAG_SUCCESS(HttpStatus.OK,"태그 생성 성공"),
	ASSIGN_TAGS_TO_MEMBER_SUCCESS(HttpStatus.OK, "사용자에게 태그 할당 성공"),
	GET_MEMBER_TAGS_SUCCESS(HttpStatus.OK, "사용자 태그 목록 조회 성공"),
	UPDATE_TAG_SUCCESS(HttpStatus.OK, "태그 수정 성공"),
	DELETE_TAG_SUCCESS(HttpStatus.OK, "태그 삭제 성공"),

	//BLOCK
	BLOCK_USER_SUCCESS(HttpStatus.OK, "유저 차단 성공"),
	UNBLOCK_USER_SUCCESS(HttpStatus.OK, "유저 차단 해제 성공"),
	GET_BLOCKED_USERS_SUCCESS(HttpStatus.OK, "차단한 사용자 목록 조회 성공"),

	//REPORT
	CREATE_REPORT_SUCCESS(HttpStatus.CREATED, "신고가 성공적으로 접수되었습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
