package com.example.luvisluvproject.domain.block.repository;

import com.example.luvisluvproject.domain.block.entity.Block;
import com.example.luvisluvproject.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {
	boolean existsByBlockerAndBlocked(Member blocker, Member blocked);
	Optional<Block> findByBlockerAndBlocked(Member blocker, Member blocked);
	List<Block> findAllByBlocker(Member blocker);
}