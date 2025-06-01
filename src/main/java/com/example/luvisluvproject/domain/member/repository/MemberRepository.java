package com.example.luvisluvproject.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luvisluvproject.domain.member.entity.Member;

import jakarta.validation.constraints.NotBlank;

public interface MemberRepository extends JpaRepository<Member, Long> {

	boolean existsByEmail(String email);

	Optional<Member> findByEmail(String email);

	boolean existsByName(@NotBlank(message = "이름을 입력해주세요.") String name);
}
