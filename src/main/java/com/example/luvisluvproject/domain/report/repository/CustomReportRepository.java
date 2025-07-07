package com.example.luvisluvproject.domain.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.luvisluvproject.admin.sanction.dto.SanctionReportDto;

public interface CustomReportRepository {

	Page<SanctionReportDto> findMemberInformationDtoByMemberId(Long memberId, Pageable pageable);
}
