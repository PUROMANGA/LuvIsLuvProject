package com.example.luvisluvproject.domain.match.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.luvisluvproject.domain.match.entity.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {
}
