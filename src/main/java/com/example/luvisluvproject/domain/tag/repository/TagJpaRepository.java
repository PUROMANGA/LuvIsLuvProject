package com.example.luvisluvproject.domain.tag.repository;

import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.domain.tag.enums.TagCategory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagJpaRepository extends JpaRepository<Tag, Long> {

	Boolean existsByName(String tagName);
	/**
	 * 이름으로 태그 조회 (중복 방지, 자동완성 등에서 활용)
	 */
	Optional<Tag> findByName(String name);

	/**
	 * 카테고리 + 활성 태그만 우선순위 순으로 조회
	 */
	List<Tag> findAllByCategoryAndActiveTrueOrderByPriorityDesc(TagCategory category);

	/**
	 * 전체 활성 태그 조회 (우선순위순)
	 */
	List<Tag> findAllByActiveTrueOrderByPriorityDesc();

	/**
	 * 전체 태그 (카테고리별 + 우선순위) 조회 - 관리자용
	 */
	List<Tag> findAllByOrderByCategoryAscPriorityDesc();
}
