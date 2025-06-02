package com.example.luvisluvproject.domain.member.entity;

import com.example.luvisluvproject.domain.chat.entity.ChatRoom;
import com.example.luvisluvproject.domain.member.enums.UserRole;
import com.example.luvisluvproject.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor
@Builder
public class Member extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false, length = 50)
	private String name;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, length = 10)
	private String birthday;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private UserRole userRole;

	@Column(nullable = false)
	private boolean status;

	//호감도
	@Column(nullable = false)
	private Long likeCount;

	public Member(String name, String email, String password, String birthday, UserRole userRole) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.birthday = birthday;
		this.userRole = userRole;
	}

	public Member(Long id, String name, String email, String password, String birthday, UserRole userRole,
		boolean status,
		Long likeCount) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.birthday = birthday;
		this.userRole = userRole;
		this.status = status;
		this.likeCount = likeCount;
	}

	/**
	 * 호감도 업!
	 */

	public void plusIsLike() {
		this.likeCount++;
	}

	public void update(String password) {
		this.password = password;
	}

	public void softDelete() {
		this.status = true;
	}
}
