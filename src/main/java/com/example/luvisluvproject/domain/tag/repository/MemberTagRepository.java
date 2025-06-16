package com.example.luvisluvproject.domain.tag.repository;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.tag.entity.MemberTag;
import com.example.luvisluvproject.domain.tag.entity.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 사용자와 태그 연결 (MemberTag) 전용 JPA Repository
 */
public interface MemberTagRepository extends JpaRepository<MemberTag, Long>, CustomMemberTagRepository {

	/**
	 * 회원 ID 기준 태그 전체 삭제
	 */
	void deleteByMemberId(Long memberId);

	/**
	 * 특정 태그를 보유한 사용자 수
	 */
	long countByTagId(Long tagId);

	/**
	 * 특정 회원이 보유한 태그 전체 조회
	 */
	List<MemberTag> findAllByMemberId(Long memberId);

	/**
	 * 특정 회원의 태그 목록 페이징 조회
	 */
	Page<MemberTag> findByMemberId(Long memberId, Pageable pageable);

	/**
	 * 태그 이름으로 연결된 모든 회원 조회
	 */
	@Query("select distinct m.member from MemberTag m where m.tag.name = :tagName")
	List<Member> findAllMemberByTag(String tagName);
}
