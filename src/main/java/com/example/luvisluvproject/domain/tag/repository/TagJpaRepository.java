package com.example.luvisluvproject.domain.tag.repository;

import com.example.luvisluvproject.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 태그 마스터 엔티티 JPA Repository
 */
public interface TagJpaRepository extends JpaRepository<Tag, Long> {
}
