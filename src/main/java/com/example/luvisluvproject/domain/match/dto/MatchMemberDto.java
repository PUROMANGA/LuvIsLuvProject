package com.example.luvisluvproject.domain.match.dto;

import com.example.luvisluvproject.domain.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MatchMemberDto {
	private Long memberId;
	private String name;

	public MatchMemberDto(Member member) {
		this.memberId = member.getId();
		this.name = member.getName();
	}
}
