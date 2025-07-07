package com.example.luvisluvproject.domain.report.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.luvisluvproject.admin.sanction.dto.SanctionReportDto;
import com.example.luvisluvproject.domain.member.dto.MemberInformationDto;
import com.example.luvisluvproject.domain.member.entity.QMember;
import com.example.luvisluvproject.domain.report.entity.QReport;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public class CustomReportRepositoryImpl implements CustomReportRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Page<SanctionReportDto> findMemberInformationDtoByMemberId(Long memberId, Pageable pageable) {

		QReport report = new QReport("r");

		List<SanctionReportDto> memberInformationDto = jpaQueryFactory.select(
			Projections.constructor(SanctionReportDto.class,
				report.id,
				report.reporter.id,
				report.reporter.email,
				report.reporter.name,
				report.reported.id,
				report.reported.email,
				report.reported.name,
				report.reason,
				report.description))
			.from(report)
			.where(report.reported.id.eq(memberId))
			.fetch();

		long total = memberInformationDto.size();

		return new PageImpl<>(memberInformationDto, pageable, total);
	}
}
