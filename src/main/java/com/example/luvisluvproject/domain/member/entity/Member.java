package com.example.luvisluvproject.domain.member.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Nullable;

import com.example.luvisluvproject.domain.member.enums.UserRole;
import com.example.luvisluvproject.domain.tag.entity.MemberTag;
import com.example.luvisluvproject.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(of = {"name", "email"})
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

	private String content;

	@Column(nullable = false)
	private boolean status;

	@Builder.Default
	@Column(nullable = false)
	private int reportCount = 0;

	//호감도
	@Builder.Default
	private Long likeCount = 0L;

	private LocalDateTime restrictedUntil;

	@OneToMany(mappedBy = "member")
	private List<MemberTag> memberTagList;

	public Member(String name, String email, String password, LocalDate birthday, UserRole userRole) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.birthday = birthday;
		this.userRole = userRole;
	}

	// 일반 유저 생성시 사용하는 생성자
	public Member(String name, String email, String password, LocalDate birthday, UserRole userRole, String content) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.birthday = birthday;
		this.userRole = userRole;
		this.status = false;
		this.likeCount = 0L;
		this.reportCount = 0;
		this.content = content;
	}

	public Member(Long id, String name, String email, String password, LocalDate birthday, UserRole userRole,
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

	public void updatePassword(String password) {
		this.password = password;
	}

	public void updateContent(String content) {
		this.content = content;
	}

	public void softDelete() {
		this.status = true;
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

	/**
	 * signupUserSuccess() 테스트용 생성자
	 */
	public Member(Long id, String name, String email, String password, LocalDate birthday, UserRole userRole) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.birthday = birthday;
		this.userRole = userRole;
	}
}
