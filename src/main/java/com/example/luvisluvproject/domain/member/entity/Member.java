package com.example.luvisluvproject.domain.member.entity;

import java.time.LocalDate;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor
@AllArgsConstructor
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

	//신고횟수

	@Column(nullable = false)
	private int reportCount = 0;

	//호감도
	private Long likeCount = 0L;

	//태그 소지 개수
	private int tagCount = 0;

	/**
	 * 일반 생성자
	 */
	public Member(String name, String email, String password, LocalDate birthday, UserRole userRole, String content,
		boolean status, int reportCount, Long likeCount, Integer tagCount) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.birthday = birthday;
		this.userRole = userRole;
		this.content = content;
		this.status = status;
		this.reportCount = reportCount;
		this.likeCount = likeCount;
		this.tagCount = tagCount;
	}

	/**
	 * 테스트용 생성자
	 */

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

	/**
	 * 호감도 업!
	 */
	public void plusIsLike() {
		this.likeCount++;
	}

	/**
	 * 회원정보수정
	 */
	public void updateProfile(String password, String content) {
		this.password = password;
		this.content = content;
	}

	public void plusTagCount(int number) {
		this.tagCount += number;
	}

	public void subTagCount(int number) {
		this.tagCount -= number;
	}

	/**
	 * 소프트 딜리트
	 */
	public void softDelete() {
		this.status = true;
	}
}
