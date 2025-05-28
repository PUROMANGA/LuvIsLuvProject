package com.example.luvisluvproject.global.error;

import java.time.LocalDateTime;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice

public class CustomExceptionHandler {

	/**
	 * Vaild 예외처리
	 */

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CustomErrorResponse> handleArgumentNotValidException(MethodArgumentNotValidException e) {

		log.error("[ValidException 발생] cause:{}, message: {}",
			NestedExceptionUtils.getMostSpecificCause(e),
			e.getMessage());

		ErrorCode errorCode = ExceptionCode.VALID_EXCEPTION;

		CustomErrorResponse response = CustomErrorResponse.builder()
			.message(errorCode.getMessage())
			.timeStamp(LocalDateTime.now())
			.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
}
