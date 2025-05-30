package com.example.luvisluvproject.domain.chat.entity;

import com.example.luvisluvproject.domain.chat.common.MessageType;
import com.example.luvisluvproject.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "messages")

public class Message extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chatroom_id")
	private ChatRoom chatRoom;

	@Column(nullable = false)
	private Long senderId;

	@Column(nullable = false)
	private Long receiverId;

	@Column(nullable = false)
	private Long isRead;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private String fileUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MessageType messageType;

	@Builder
	public Message(MessageType messageType, String content, ChatRoom chatRoom, Long senderId) {
		this.messageType = messageType;
		this.content = content;
		this.chatRoom = chatRoom;
		this.senderId = senderId;
	}
}
