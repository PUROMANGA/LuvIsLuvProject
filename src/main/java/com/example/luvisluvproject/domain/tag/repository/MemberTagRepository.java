package com.example.luvisluvproject.domain.tag.repository;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.tag.entity.MemberTag;
import com.example.luvisluvproject.domain.tag.entity.Tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberTagRepository extends JpaRepository<MemberTag, Long>, CustomMemberTagRepository {

	/**
	 * 유저가 선택한 태그 전체 삭제
	 */
	void deleteByMemberId(Long memberId);

	/**
	 * 태그가 몇 명의 유저에게 연결되어 있는지 (사용 여부 판단)
	 */
	long countByTagId(Long tagId);

	/**
	 * 특정 유저가 연결한 모든 태그 정보
	 */
	List<MemberTag> findAllByMemberId(Long memberId);

	@Query("select distinct  m.member from MemberTag m where m.tag.name = :tagName")
	List<Member> findAllMemberByTag(String tagName);

	List<MemberTag> tag(Tag tag);
}
