package com.example.luvisluvproject.domain.chat.entity;

import java.util.List;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.global.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ChatRooms")

public class ChatRoom extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MemberChatRoom> memberChatRoomRList;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_a_id")
	private Member memberA;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_b_id")
	private Member memberB;

	public ChatRoom(Member memberA, Member memberB) {
		this.memberA = memberA;
		this.memberB = memberB;
	}

	public Member checkMember(Member me) {
		Member opponent;
		if (me.getId().equals(memberA.getId())) {
			opponent = memberB;
		} else {
			opponent = memberA;
		}
		return opponent;
	}

	public ChatRoom(Long id, List<MemberChatRoom> memberChatRoomRList, Member memberA, Member memberB) {
		this.id = id;
		this.memberChatRoomRList = memberChatRoomRList;
		this.memberA = memberA;
		this.memberB = memberB;
	}

	public ChatRoom(List<MemberChatRoom> memberChatRoomRList, Member memberA, Member memberB) {
		this.memberChatRoomRList = memberChatRoomRList;
		this.memberA = memberA;
		this.memberB = memberB;
	}
}
