package com.example.luvisluvproject.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.luvisluvproject.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	boolean existsByEmail(String email);

	Optional<Member> findByEmail(String email);

	boolean existsByName(String name);

	@Query("SELECT m.tagCount FROM Member m WHERE m.id = :id")
	int findTagCountById(Long id);
}
