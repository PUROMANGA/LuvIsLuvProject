package com.example.luvisluvproject.domain.chat.dto;

import java.time.LocalDateTime;

import com.example.luvisluvproject.domain.chat.common.MessageType;
import com.example.luvisluvproject.domain.chat.entity.Message;

import lombok.Getter;

@Getter

public class ResponseMessageDto {

	private final String id;
	private final Long chatRoomId;
	private final Long senderId;
	private final Boolean isRead;
	private final String content;
	private final String fileUrl;
	private final MessageType messageType;
	private final LocalDateTime creatTime;
	private final LocalDateTime modifiedTime;

	public ResponseMessageDto(Message message) {
		this.id = message.getId();
		this.chatRoomId = message.getChatRoomId();
		this.senderId = message.getSenderId();
		this.isRead = message.getIsRead();
		this.content = message.getContent();
		this.fileUrl = message.getFileUrl();
		this.messageType = message.getMessageType();
		this.creatTime = message.getCreatTime();
		this.modifiedTime = message.getModifiedTime();
	}
}
