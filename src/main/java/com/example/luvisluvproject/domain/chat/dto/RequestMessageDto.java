package com.example.luvisluvproject.domain.chat.dto;

import com.example.luvisluvproject.domain.chat.common.MessageType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor

public class RequestMessageDto {
	private String content;
	private String fileUrl;
	private MessageType messageType;
}
