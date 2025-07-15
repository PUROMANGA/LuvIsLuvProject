package com.example.luvisluvproject.domain.tag.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.luvisluvproject.domain.match.dto.ResponseMatchMemberDto;
import com.example.luvisluvproject.domain.member.entity.QMember;
import com.example.luvisluvproject.domain.membermatchinfo.QMemberMatchInfo;
import com.example.luvisluvproject.domain.tag.entity.QMemberTag;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public class CustomMemberTagRepositoryImpl implements CustomMemberTagRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<ResponseMatchMemberDto> findResponseMatchMemberDtoFindById(Long id) {
		QMemberTag mt1 = new QMemberTag("mt1");
		QMemberTag mt2 = new QMemberTag("mt2");
		QMember m1 = new QMember("m1");
		QMemberMatchInfo mmi = new QMemberMatchInfo("mmi");

		long t2 = System.nanoTime();

		List<Tuple> tuples = jpaQueryFactory
			.select(
				m1.id,
				m1.name,
				m1.content,
				mmi.isBlocked,
				mmi.isMatched,
				mmi.recommendedTime,
				mmi.recommendedExposureScore)
			.from(mt1)
			.join(mt2).on(mt2.tagId.eq(mt1.tagId))
			.join(m1).on(m1.id.eq(mt2.memberId))
			.leftJoin(mmi).on(mmi.meId.eq(mt1.memberId).and(mmi.targetId.eq(mt2.memberId)))
			.where(mt1.memberId.eq(id))
			.fetch();

		long t3 = System.nanoTime();

		List<ResponseMatchMemberDto> responseMatchMemberDtoList = filterResponseMatchMemberDto(id, tuples);

		long t4 = System.nanoTime();
		System.out.println("responseMatchMemberDtoList = " + responseMatchMemberDtoList);
		System.out.println("⏱️ 쿼리 실행 시간: " + ((t3 - t2) / 1_000_000) + "ms");
		System.out.println("⏱️ DTO 매핑 시간: " + ((t4 - t3) / 1_000_000) + "ms");
		System.out.println("⏱️ 전체 소요 시간: " + ((t4 - t2) / 1_000_000) + "ms");

		return responseMatchMemberDtoList;
	}

	@Override
	public List<ResponseMatchMemberDto> findRecommendedMembersByMeIdAndMemberIds(Long meId, List<Long> ids) {
		QMember m1 = new QMember("m1");
		QMemberMatchInfo mmi = new QMemberMatchInfo("mmi");
		QMemberMatchInfo mmiSub = new QMemberMatchInfo("mmiSub");

		List<Tuple> tuples = jpaQueryFactory
			.select(
				m1.id,
				m1.name,
				m1.content,
				mmi.isBlocked,
				mmi.isMatched,
				mmi.recommendedTime,
				mmi.recommendedExposureScore)
			.from(m1)
			.join(mmi).on(m1.id.eq(mmi.targetId))
			.where(m1.id.in(
				JPAExpressions
					.select(mmiSub.targetId)
					.from(mmiSub)
					.where(mmiSub.meId.in(ids), mmiSub.isMatched.isTrue())
			))
			.fetch();

		return filterResponseMatchMemberDto(meId, tuples);
	}

	public List<ResponseMatchMemberDto> filterResponseMatchMemberDto(Long meId, List<Tuple> tuples) {
		QMember m1 = new QMember("m1");
		QMemberMatchInfo mmi = new QMemberMatchInfo("mmi");

		LocalDateTime limitTime = LocalDateTime.now().minusHours(48);

		return tuples.stream()
				.filter(t -> !t.get(m1.id).equals(meId))
				.filter(t -> t.get(mmi.isBlocked) == null || Boolean.FALSE.equals(t.get(mmi.isBlocked)))
				.filter(t -> t.get(mmi.isMatched) == null || Boolean.FALSE.equals(t.get(mmi.isMatched)))
				.filter(t -> t.get(mmi.recommendedTime) == null || t.get(mmi.recommendedTime).isBefore(limitTime))
				.filter(t-> t.get(mmi.recommendedExposureScore) == null || t.get(mmi.recommendedExposureScore) < 4)
				.map(t -> new ResponseMatchMemberDto(
					t.get(m1.id),
					t.get(m1.name),
					t.get(m1.content),
					t.get(mmi.isBlocked),
					t.get(mmi.isMatched),
					t.get(mmi.recommendedTime),
					t.get(mmi.recommendedExposureScore)))
			.collect(Collectors.toList());
	}
}
