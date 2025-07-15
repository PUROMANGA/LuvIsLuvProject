package com.example.luvisluvproject.domain.membermatchinfo;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberMatchInfoRepository extends JpaRepository<MemberMatchInfo, Long> {

	List<MemberMatchInfo> findAllByMeIdAndTargetIdIn(Long meId, Set<Long> ids);
}
