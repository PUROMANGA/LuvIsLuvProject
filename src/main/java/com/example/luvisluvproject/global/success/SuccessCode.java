package com.example.luvisluvproject.global.success;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {
	FIND_MEMBER_SUCCESS(HttpStatus.OK, "회원 조회 성공"),
	UPDATE_MEMBER_SUCCESS(HttpStatus.OK, "회원정보 수정 성공"),
	DELETE_MEMBER_SUCCESS(HttpStatus.OK, "회원 탈퇴 완료");

	private final HttpStatus httpStatus;
	private final String message;
}
