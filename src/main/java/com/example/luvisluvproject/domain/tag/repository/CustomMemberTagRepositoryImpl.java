package com.example.luvisluvproject.domain.tag.repository;

import java.util.List;

import com.example.luvisluvproject.domain.match.dto.ResponseMatchMemberDto;
import com.example.luvisluvproject.domain.member.entity.QMember;
import com.example.luvisluvproject.domain.tag.entity.QMemberTag;
import com.example.luvisluvproject.domain.tag.entity.QTag;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public class CustomMemberTagRepositoryImpl implements CustomMemberTagRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<ResponseMatchMemberDto> findResponseMatchMemberDtoFindByEmail(String email) {
		QMemberTag mt1 = new QMemberTag("mt1");
		QMemberTag mt2 = new QMemberTag("mt2");
		QMember m1 = new QMember("m1");
		QMember m2 = new QMember("m2");

		return jpaQueryFactory
			.select(Projections.constructor(ResponseMatchMemberDto.class,
				m2.id,
				m2.name,
				m2.content
			))
			.from(mt2)
			.join(mt1).on(mt1.tagName.eq(mt2.tagName))
			.join(m1).on(mt1.memberId.eq(m1.id))
			.join(m2).on(mt2.memberId.eq(m2.id))
			.where(m1.email.eq(email)
				.and(m2.email.ne(email)))
			.groupBy(m2.id)
			.orderBy(m2.likeCount.desc())
			.limit(10)
			.fetch();
	}
}
