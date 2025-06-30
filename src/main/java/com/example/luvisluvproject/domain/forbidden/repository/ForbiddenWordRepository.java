package com.example.luvisluvproject.domain.forbidden.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luvisluvproject.domain.forbidden.entity.ForbiddenWord;

public interface ForbiddenWordRepository extends JpaRepository<ForbiddenWord, Long> {
}
