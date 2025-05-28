package com.example.luvisluvproject.domain.chat.entity;

import com.example.luvisluvproject.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class ChatRoom extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String chatName;

	@Column(nullable = false)
	private Long memberAId;

	@Column(nullable = false)
	private Long memberBId;
}
