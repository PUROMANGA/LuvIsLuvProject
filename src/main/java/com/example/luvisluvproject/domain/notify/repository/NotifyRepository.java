package com.example.luvisluvproject.domain.notify.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.luvisluvproject.domain.notify.entity.Notify;

public interface NotifyRepository extends JpaRepository<Notify, Long> {

	@Query("select n from Notify n where (n.receiver.id = :memberId OR n.sender.id = :memberId) and n.isRead = false ")
	Slice<Notify> findByMemberId(Long memberId, Pageable pageable);
}
