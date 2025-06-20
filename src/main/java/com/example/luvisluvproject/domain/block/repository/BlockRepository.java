package com.example.luvisluvproject.domain.block.repository;

import com.example.luvisluvproject.domain.block.entity.Block;
import com.example.luvisluvproject.domain.member.entity.Member;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * BlockRepository
 *
 * 사용자 간의 차단 정보를 DB에서 조회하거나 저장하는 JPA 리포지토리입니다.
 * Block 엔티티와 관련된 기본 및 커스텀 조회 기능을 제공합니다.
 */
public interface BlockRepository extends JpaRepository<Block, Long> {

	/**
	 * 특정 사용자가 다른 사용자를 차단했는지 여부 확인
	 *
	 * @param blocker 차단을 수행한 사용자
	 * @param blocked 차단당한 사용자
	 * @return 차단 관계가 존재하면 true, 없으면 false
	 */
	boolean existsByBlockerAndBlocked(Member blocker, Member blocked);

	/**
	 * 두 사용자 간의 차단 정보를 조회
	 *
	 * @param blocker 차단을 수행한 사용자
	 * @param blocked 차단당한 사용자
	 * @return Block 정보 (없을 경우 Optional.empty())
	 */
	Optional<Block> findByBlockerAndBlocked(Member blocker, Member blocked);

	/**
	 * 특정 사용자가 차단한 모든 사용자 목록 조회
	 *
	 * @param blocker 차단을 수행한 사용자
	 * @return 차단한 사용자들의 Block 엔티티 리스트
	 * unblocked = false 조건 Slice 조회
	 */
	Slice<Block> findAllByBlocker(Member blocker, Pageable pageable);
}
