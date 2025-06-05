package com.example.luvisluvproject.domain.chat.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.luvisluvproject.domain.chat.common.MessageType;
import com.example.luvisluvproject.domain.chat.dto.RequestMessageDto;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(collection = "messages")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor

public class Message {

	@Id
	private String id;

	private Long chatRoomId;
	private Long senderId;
	private Long receiverId;
	private String content;
	private String fileUrl;
	private MessageType messageType;
	private Boolean isRead;
	@CreatedDate
	private LocalDateTime creatTime;
	@LastModifiedDate
	private LocalDateTime modifiedTime;

	public Message(String id, Long chatRoomId, Long senderId, Long receiverId, String content, String fileUrl,
		MessageType messageType, Boolean isRead, LocalDateTime creatTime, LocalDateTime modifiedTime) {
		this.id = id;
		this.chatRoomId = chatRoomId;
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.content = content;
		this.fileUrl = fileUrl;
		this.messageType = messageType;
		this.isRead = isRead;
		this.creatTime = creatTime;
		this.modifiedTime = modifiedTime;
	}

	public Message(Long chatRoomId, Long senderId, Long receiverId, RequestMessageDto requestMessageDto,
		Boolean isRead) {
		this.chatRoomId = chatRoomId;
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.content = requestMessageDto.getContent();
		this.fileUrl = requestMessageDto.getFileUrl();
		this.messageType = requestMessageDto.getMessageType();
		this.isRead = isRead;
	}

	public void updateIsRead() {
		this.isRead = true;
	}
}
