package com.example.luvisluvproject.domain.chat.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luvisluvproject.domain.chat.entity.MemberChatRoom;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {
	MemberChatRoom findByMemberEmailAndChatRoomId(String email, Long chatRoomId);
	Slice<MemberChatRoom> findAllByMemberIdAndDeletedFalse(Long id, Pageable pageable);
	int countMemberChatRoomsByMemberId(Long id);
	Boolean existsByMemberEmailAndChatRoom_Id(String email, Long chatRoomId);
}
