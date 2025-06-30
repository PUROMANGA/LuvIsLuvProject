package com.example.luvisluvproject.domain.notify.dto;

import com.example.luvisluvproject.domain.chat.dto.MessageDto;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.notify.NotifyCategory;
import com.example.luvisluvproject.domain.notify.entity.Notify;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class NotifyDto {

	//보내는 사람Id
	private final Long meId;

	//보내는 사람이름
	private final String senderName;

	//받는 사람ID
	private final Long receivedId;

	//받는 사람이름
	private final String receiverName;

	private final NotifyCategory content;

	private final boolean isRead;

	public NotifyDto(Notify notify, Long memberId) {
		if (notify.getReceiver().getId().equals(memberId)) {
			this.meId = notify.getSender().getId();
			this.senderName = notify.getSender().getName();
			this.receivedId = notify.getReceiver().getId();
			this.receiverName = notify.getReceiver().getName();
		} else {
			this.meId = notify.getReceiver().getId();
			this.senderName = notify.getReceiver().getName();
			this.receivedId = notify.getSender().getId();
			this.receiverName = notify.getSender().getName();
		}
		this.content = notify.getContent();
		this.isRead = true;
	}

	public NotifyDto(Member me, Member receiverMember, NotifyCategory content, boolean isRead) {
		this.meId = me.getId();
		this.senderName = me.getName();
		this.receivedId = receiverMember.getId();
		this.receiverName = receiverMember.getName();
		this.content = content;
		this.isRead = isRead;
	}
}
