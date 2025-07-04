package com.example.luvisluvproject.domain.tag.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luvisluvproject.domain.tag.entity.MemberTag;

/**
 * 사용자와 태그 연결 (MemberTag) 전용 JPA Repository
 */
public interface MemberTagRepository extends JpaRepository<MemberTag, Long>, CustomMemberTagRepository {

	List<MemberTag> findAllByMemberId(Long memberId);

	boolean existsByMemberIdAndTagNameIn(Long memberId, Set<String> tagName);

	Optional<MemberTag> findByMemberIdAndTagName(Long memberId, String tagName);
}
