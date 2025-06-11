package com.example.luvisluvproject.domain.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luvisluvproject.domain.chat.entity.ChatRoom;
import com.example.luvisluvproject.domain.chat.entity.MemberChatRoom;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {
	List<MemberChatRoom> findAllByMemberId(Long id);

	int countMemberChatRoomsByMemberId(Long id);
}
