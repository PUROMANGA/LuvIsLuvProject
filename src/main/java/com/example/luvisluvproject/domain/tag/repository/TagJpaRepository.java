package com.example.luvisluvproject.domain.tag.repository;

import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.domain.tag.enums.TagCategory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagJpaRepository extends JpaRepository<Tag, Long> {

	/**
	 * 태그 이름으로 중복 여부를 확인하거나 검색할 때 사용
	 */
	Optional<Tag> findByName(String name);

	/**
	 * 카테고리별 태그 전체 조회 (활성된 것만)
	 */
	List<Tag> findAllByCategoryAndActiveTrueOrderByPriorityDesc(TagCategory category);

	/**
	 * 전체 태그 중 활성화된 태그만 (우선순위 높은 순)
	 */
	List<Tag> findAllByActiveTrueOrderByPriorityDesc();

	/**
	 * 관리자가 전체 태그를 확인할 때 (카테고리 상관없이 모두 가져옴)
	 */
	List<Tag> findAllByOrderByCategoryAscPriorityDesc();
}
