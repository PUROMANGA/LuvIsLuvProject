package com.example.luvisluvproject.domain.block.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BlockResponseDto {
	private String message;
	private Long blockedId;
	private LocalDateTime blockTime;
}