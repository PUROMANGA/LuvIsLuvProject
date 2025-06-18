package com.example.luvisluvproject.global.success;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

	private final String message;
	private final String status;
	private final T data;

	public ApiResponse(String message, String status, T data) {
		this.message = message;
		this.status = status;
		this.data = data;
	}

	public static <T> ApiResponse<T> of(SuccessCode successCode, T data) {
		return new ApiResponse<>(
			successCode.getMessage(),
			successCode.name(),
			data
		);
	}

	public static <T> ApiResponse<T> of(SuccessCode code) {
		return new ApiResponse<>(
			code.getMessage(),
			code.name(),
			null
		);
	}
}
