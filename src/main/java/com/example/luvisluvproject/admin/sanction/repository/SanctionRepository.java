package com.example.luvisluvproject.admin.sanction.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luvisluvproject.admin.sanction.entity.Sanction;

public interface SanctionRepository extends JpaRepository<Sanction, Long> {
	Optional<Sanction> findByMemberId(Long memberId);
}
