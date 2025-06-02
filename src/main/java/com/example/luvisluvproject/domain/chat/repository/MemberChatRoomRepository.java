package com.example.luvisluvproject.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luvisluvproject.domain.chat.entity.MemberChatRoom;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {
}
