package com.example.luvisluvproject.domain.tag.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.domain.tag.enums.TagCategory;

public interface TagJpaRepository extends JpaRepository<Tag, Long> {

	Boolean existsByName(String tagName);

	/**
	 * 이름으로 태그 조회 (중복 방지, 자동완성 등에서 활용)
	 */
	Optional<Tag> findByName(String name);

}
