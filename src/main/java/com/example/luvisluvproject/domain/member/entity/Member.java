package com.example.luvisluvproject.domain.member.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.luvisluvproject.domain.member.enums.UserRole;
import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.global.common.BaseEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor
@AllArgsConstructor
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
	private LocalDate birthday;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private UserRole userRole;

	@Column(nullable = false)
	private boolean status;

	@Column(nullable = false)
	private Long likeCount;

	@Column(nullable = false)
	private int reportCount = 0;

	private LocalDateTime restrictedUntil;

	@ManyToMany
	@JoinTable(
		name = "member_tags",
		joinColumns = @JoinColumn(name = "member_id"),
		inverseJoinColumns = @JoinColumn(name = "tag_id")
	)
	private List<Tag> tags = new ArrayList<>();

	// 생성자
	public Member(String name, String email, String password, LocalDate birthday, UserRole userRole) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.birthday = birthday;
		this.userRole = userRole;
	}

	public Member(Long id, String name, String email, String password, LocalDate birthday, UserRole userRole,
		boolean status, Long likeCount) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.birthday = birthday;
		this.userRole = userRole;
		this.status = status;
		this.likeCount = likeCount;
	}

	public Member(String name, String email, String password, LocalDate birthday, UserRole userRole, boolean status,
		Long likeCount) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.birthday = birthday;
		this.userRole = userRole;
		this.status = status;
		this.likeCount = likeCount;
	}

	// 호감도 증가
	public void plusIsLike() {
		this.likeCount++;
	}

	// 비밀번호 변경
	public void update(String password) {
		this.password = password;
	}

	// 소프트 삭제
	public void softDelete() {
		this.status = true;
	}

	// 신고 횟수 증가 및 제한 설정
	public void increaseReportCount() {
		this.reportCount++;
		if (this.reportCount >= 3 && this.restrictedUntil == null) {
			this.restrictedUntil = LocalDateTime.now().plusDays(30);
		}
	}

	// 활동 제한 상태인지 확인
	public boolean isRestricted() {
		return this.restrictedUntil != null && this.restrictedUntil.isAfter(LocalDateTime.now());
	}
}
