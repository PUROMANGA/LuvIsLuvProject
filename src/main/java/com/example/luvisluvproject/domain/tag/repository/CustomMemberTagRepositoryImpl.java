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
		QRecommendedMembers recommendedMembers = new QRecommendedMembers("rm");
		QBlock b = new QBlock("b");
		QMatch match = new QMatch("mc");

		return jpaQueryFactory
			.select(Projections.constructor(ResponseMatchMemberDto.class,
				m2.id,
				m2.name,
				m2.content
			))
			.from(mt2)
			.join(mt1)
			.on(mt1.tagName.eq(mt2.tagName))
			.join(m1)
			.on(mt1.memberId.eq(m1.id))
			.join(m2)
			.on(mt2.memberId.eq(m2.id))
			.leftJoin(recommendedMembers)
			.on(recommendedMembers.memberId.eq(m1.id).and(recommendedMembers.recommendedId.eq(m2.id)))
			.leftJoin(b)
			.on(b.blocker.id.eq(m1.id).and(b.blocked.id.eq(m2.id)))
			.leftJoin(match)
			.on(match.senderId.eq(m1.id).and(match.receiverId.eq(m2.id)))
			.where(m1.email.eq(email)
				.and(m2.email.ne(email))
				.and(recommendedMembers.exposureScore.loe(3).or(recommendedMembers.exposureScore.isNull()))
				.and(match.id.isNull())
				.and(b.id.isNull()))
			.groupBy(m2.id)
			.orderBy(m2.likeCount.desc())
			.limit(10)
			.fetch();
	}

	@Override
	public List<ResponseMatchMemberDto> findCoRecommendedMembersByMeEmailAndEmail(String meEmail,String email) {
		QMemberTag mt1 = new QMemberTag("mt1");
		QMemberTag mt2 = new QMemberTag("mt2");
		QMember m1 = new QMember("m1");
		QMember m2 = new QMember("m2");
		QBlock b = new QBlock("b");
		QBlock b2 = new QBlock("b2");
		QRecommendedMembers recommendedMembers = new QRecommendedMembers("rm");
		QMatch match = new QMatch("mc");

		return jpaQueryFactory.select(Projections.constructor(ResponseMatchMemberDto.class,
			m2.id,
			m2.name,
			m2.content))
			.from(mt2)
			.join(mt1).on(mt1.tagName.eq(mt2.tagName))
			.join(m1).on(m1.id.eq(mt1.memberId))
			.join(m2).on(m2.id.eq(mt2.memberId))
			.leftJoin(recommendedMembers).on(recommendedMembers.memberId.eq(m1.id).and(recommendedMembers.recommendedId.eq(m2.id)))
			.leftJoin(b).on(b.blocker.id.eq(m1.id).and(b.blocked.id.eq(m2.id)))
			.leftJoin(b2).on(b2.blocker.email.eq(meEmail).and(b2.blocked.id.eq(m2.id)))
			.leftJoin(match)
			.on(match.senderId.eq(m1.id).and(match.receiverId.eq(m2.id)))
			.where(m1.email.eq(email)
				.and(m2.email.ne(email))
				.and(m2.email.ne(meEmail))
				.and(recommendedMembers.exposureScore.loe(3).or(recommendedMembers.exposureScore.isNull()))
				.and(b.id.isNull())
				.and(b2.id.isNull())
				.and(b.id.isNull()))
			.groupBy(m2.id)
			.orderBy(m2.likeCount.desc())
			.fetch();
	}
}
