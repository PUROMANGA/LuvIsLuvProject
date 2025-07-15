package com.example.luvisluvproject.domain.tag.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.domain.tag.enums.TagCategory;

public interface TagJpaRepository extends JpaRepository<Tag, Long> {

	Boolean existsByName(String tagName);

	Set<Tag> findByNameIn(Set<String> tags);
}
