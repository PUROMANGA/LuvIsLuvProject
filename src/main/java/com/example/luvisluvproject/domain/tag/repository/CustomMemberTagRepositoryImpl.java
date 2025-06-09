package com.example.luvisluvproject.domain.tag.repository;

import java.util.List;

import com.example.luvisluvproject.domain.match.dto.MatchMemberDto;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.tag.entity.QMemberTag;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public class CustomMemberTagRepositoryImpl  implements CustomMemberTagRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<MatchMemberDto> findMatchMemberDtoFindByEmail(String email) {
		QMemberTag mt1 = new QMemberTag("mt1");
		QMemberTag mt2 = new QMemberTag("mt2");

		List<String> tagName = jpaQueryFactory
			.select(mt1.tag.name)
			.from(mt1)
			.where(mt1.member.email.eq(email))
			.fetch();

		List<Member> memberList = jpaQueryFactory
			.select(mt2.member)
			.from(mt2)
			.where(mt2.tag.name.in(tagName)
			.and(mt2.member.email.ne(email)))
			.fetch();

		return memberList.stream().map(MatchMemberDto::new).toList();
	}
}
