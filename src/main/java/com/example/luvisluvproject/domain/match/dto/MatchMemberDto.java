package com.example.luvisluvproject.domain.match.dto;

import java.util.ArrayList;
import java.util.List;

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

	private List<Member> memberList = new ArrayList<>();

	public MatchMemberDto(Member member) {
		this.memberList = new ArrayList<>();
		this.memberList.add(member);
	}
}
