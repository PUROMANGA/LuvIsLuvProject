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

	/**
	 * 소프트 딜리트
	 */
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

	public Member(String name, String email, String password, LocalDate birthday, UserRole userRole) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.birthday = birthday;
		this.userRole = userRole;
		this.likeCount = 0L;
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
	 * 테스트용 생성자
	 */

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
