package com.example.luvisluvproject.domain.chat.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatEnterRequest {
	private final Long roomId;
	private final Long messageId;
	private final Long memberId;
}
