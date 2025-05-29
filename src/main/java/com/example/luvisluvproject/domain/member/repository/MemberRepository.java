package com.example.luvisluvproject.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.luvisluvproject.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
  
	boolean existsByEmail(String email);
	Member findByEmail(String email);
  
}
