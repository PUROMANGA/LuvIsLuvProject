package com.example.luvisluvproject.domain.chat.dto;

import com.example.luvisluvproject.domain.chat.common.MessageType;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequestMessageDto {
	private String content;
	private String fileUrl;
	private MessageType messageType;

	public RequestMessageDto(String content, String fileUrl) {
		this.content = content;
		if(fileUrl != null) {
			this.fileUrl = fileUrl;
			this.messageType = MessageType.IMAGE;
		} else {
			this.fileUrl = null;
			this.messageType = MessageType.TEXT;
		}
	}
}
