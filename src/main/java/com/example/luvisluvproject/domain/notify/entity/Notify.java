package com.example.luvisluvproject.domain.notify.entity;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.notify.NotifyCategory;
import com.example.luvisluvproject.domain.notify.dto.NotifyDto;
import com.example.luvisluvproject.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "notifies")
public class Notify extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private NotifyCategory content;

	@Column(nullable = false)
	private Boolean isRead;

	@ManyToOne
	@JoinColumn(name = "memberA_id")
	private Member receiver;

	@ManyToOne
	@JoinColumn(name = "memberB_id")
	private Member sender;

	public Notify(NotifyDto notifyDto, Member receiver, Member sender) {
		this.content = notifyDto.getContent();
		this.isRead = notifyDto.isRead();
		this.receiver = receiver;
		this.sender = sender;
	}
}
