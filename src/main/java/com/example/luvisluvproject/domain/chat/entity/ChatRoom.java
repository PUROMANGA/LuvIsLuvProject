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
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ChatRooms")

public class ChatRoom extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_a_id")
	private Member memberA;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_b_id")
	private Member memberB;

	private int deleteCount = 0;

	public ChatRoom(Member memberA, Member memberB, int deleteCount) {
		this.memberA = memberA;
		this.memberB = memberB;
		this.deleteCount = deleteCount;
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

	public ChatRoom(Long id, Member memberA, Member memberB, int deleteCount) {
		this.id = id;
		this.memberA = memberA;
		this.memberB = memberB;
		this.deleteCount = deleteCount;
	}

	public void plusDeleteCount() {
		this.deleteCount++;
	}
}
