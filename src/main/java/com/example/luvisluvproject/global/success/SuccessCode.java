package com.example.luvisluvproject.global.success;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {
	//회원
	FIND_MEMBER_SUCCESS(HttpStatus.OK, "회원 조회 성공"),
	UPDATE_MEMBER_SUCCESS(HttpStatus.OK, "회원정보 수정 성공"),
	DELETE_MEMBER_SUCCESS(HttpStatus.OK, "회원 탈퇴 완료"),

	//리뷰
	CREATE_REVIEW_SUCCESS(HttpStatus.OK, "리뷰 작성 성공"),
	UPDATE_REVIEW_SUCCESS(HttpStatus.OK, "리뷰 수정 성공"),
	GET_REVIEW_SUCCESS(HttpStatus.OK, "리뷰 상세 조회 성공"),
	GET_ALL_REVIEWS_SUCCESS(HttpStatus.OK, "리뷰 전체 조회 성공"),
	DELETE_REVIEW_SUCCESS(HttpStatus.OK, "리뷰 전체 조회 성공");

	private final HttpStatus httpStatus;
	private final String message;
}
