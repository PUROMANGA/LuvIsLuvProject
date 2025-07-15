package com.example.luvisluvproject.domain.member.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.luvisluvproject.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	boolean existsByEmail(String email);

	Optional<Member> findByEmail(String email);

	boolean existsByName(String name);

	@Query("select m from Member m where m.reportCount > 0 and m.status = false ")
	Page<Member> findAllReportedInactiveMembers(Pageable pageable);

}
