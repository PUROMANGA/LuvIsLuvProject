package com.example.luvisluvproject.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BlockErrorCode implements ErrorCode {

	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
	ALREADY_BLOCKED(HttpStatus.BAD_REQUEST, "이미 차단한 사용자입니다."),
	BLOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "차단 정보가 존재하지 않습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	BlockErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
