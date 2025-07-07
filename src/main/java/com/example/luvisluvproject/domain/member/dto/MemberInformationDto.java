package com.example.luvisluvproject.domain.member.dto;

import java.time.LocalDate;

import com.example.luvisluvproject.domain.member.entity.Member;

import lombok.Getter;

@Getter
public class MemberInformationDto {

	private Long memberId;

	private String memberEmail;

	private String memberName;

	private LocalDate birthday;

	private int reportCount;

	private Long likeCount;

	public MemberInformationDto(Member member) {
		this.memberId = member.getId();
		this.memberEmail = member.getEmail();
		this.memberName = member.getName();
		this.birthday = member.getBirthday();
		this.reportCount = member.getReportCount();
		this.likeCount = member.getLikeCount();
	}
}
