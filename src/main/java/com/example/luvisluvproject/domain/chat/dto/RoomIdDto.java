package com.example.luvisluvproject.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomIdDto {
	private Long roomId;
	private String opponentName;
	private String message;
	private Long userId;
}
