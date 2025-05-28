package com.example.luvisluvproject.global.error;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder

public class CustomErrorResponse {
	private String message;
	private LocalDateTime timeStamp;
}
