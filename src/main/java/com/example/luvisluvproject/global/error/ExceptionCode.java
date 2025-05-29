package com.example.luvisluvproject.global.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor

public enum ExceptionCode implements ErrorCode {

	VALID_EXCEPTION(HttpStatus.BAD_REQUEST, "VALID_EXCEPTION가 발생했습니다, 인텔리제이 로그를 확인해주세요"),

	// auth
	EMAIL_ALREADY_EXIST(HttpStatus.CONFLICT, "중복된 이메일입니다."),
	USER_ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "지원하지 않는 사용자 역할입니다.");

	//가게
	STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 가게를 찾을 수 없습니다."),
	CANT_FIND_INTERFACE(HttpStatus.NOT_FOUND, "해당 정보를 찾을 수 없습니다"),
  
	//유저(=멤버)
	USER_CANT_FIND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다");

	private final HttpStatus httpStatus;
	private final String message;

	@Override
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
