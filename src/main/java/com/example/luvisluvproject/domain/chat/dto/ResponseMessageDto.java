package com.example.luvisluvproject.domain.chat.dto;

import java.time.LocalDateTime;

import com.example.luvisluvproject.domain.chat.common.MessageType;
import com.example.luvisluvproject.domain.chat.entity.Message;

import lombok.Getter;

@Getter

public class ResponseMessageDto {

	private Long id;
	private Long chatRoomId;
	private Long senderId;
	private Long receiverId;
	private Boolean isRead;
	private String content;
	private String fileUrl;
	private MessageType messageType;
	private LocalDateTime creatTime;
	private LocalDateTime modifiedTime;

	public ResponseMessageDto(Message message) {
		this.id = message.getId();
		this.chatRoomId = message.getChatRoom().getId();
		this.senderId = message.getSenderId();
		this.receiverId = message.getReceiverId();
		this.isRead = message.getIsRead();
		this.content = message.getContent();
		this.fileUrl = message.getFileUrl();
		this.messageType = message.getMessageType();
		this.creatTime = message.getCreatTime();
		this.modifiedTime = message.getModifiedTime();
	}
}
