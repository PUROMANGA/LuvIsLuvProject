package com.example.luvisluvproject.domain.match.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luvisluvproject.domain.match.entity.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {
}
