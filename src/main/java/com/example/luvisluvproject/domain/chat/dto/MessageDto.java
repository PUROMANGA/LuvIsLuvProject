package com.example.luvisluvproject.domain.chat.dto;

import com.example.luvisluvproject.domain.chat.common.MessageType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MessageDto {
	private String content;
	private String fileUrl;
	private MessageType messageType;
	private Long userId;
	private Long roomId;
	private String senderName;

	public MessageDto(String content, String fileUrl, Long userId, Long roomId, String senderName) {
		this.content = content;
		if(fileUrl != null) {
			this.fileUrl = fileUrl;
			this.messageType = MessageType.IMAGE;
		} else {
			this.fileUrl = null;
			this.messageType = MessageType.TEXT;
		}
		this.userId = userId;
		this.roomId = roomId;
		this.senderName = senderName;
	}
}

