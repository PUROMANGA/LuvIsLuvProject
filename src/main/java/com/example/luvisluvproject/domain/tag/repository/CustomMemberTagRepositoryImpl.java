package com.example.luvisluvproject.domain.tag.repository;

import java.util.List;

import com.example.luvisluvproject.domain.match.dto.ResponseMatchMemberDto;
import com.example.luvisluvproject.domain.tag.entity.QMemberTag;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public class CustomMemberTagRepositoryImpl  implements CustomMemberTagRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<ResponseMatchMemberDto> findResponseMatchMemberDtoFindByEmail(String email) {
		QMemberTag mt1 = new QMemberTag("mt1");
		QMemberTag mt2 = new QMemberTag("mt2");

		return jpaQueryFactory
			.select(Projections.constructor(ResponseMatchMemberDto.class,
				mt2.member.id, mt2.member.name, mt2.member.content))
			.from(mt2)
			.join(mt1).on(mt1.tag.eq(mt2.tag))
			.where(mt1.member.email.eq(email)
				.and(mt2.member.email.ne(email)))
			.limit(10)
			.groupBy(mt2.member.id)
			.orderBy(mt2.member.likeCount.desc())
			.fetch();
	}
}
