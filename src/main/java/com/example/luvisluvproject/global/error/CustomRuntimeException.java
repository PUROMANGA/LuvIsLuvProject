package com.example.luvisluvproject.global.error;

import lombok.Getter;

@SuppressWarnings("checkstyle:WhitespaceAround")
@Getter

public class CustomRuntimeException extends RuntimeException {
	private final ExceptionCode exceptionCode;

	public CustomRuntimeException(ExceptionCode exceptionCode) {
		super(exceptionCode.getMessage());
		this.exceptionCode = exceptionCode;
	}
}