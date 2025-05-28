package com.example.luvisluvproject.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.luvisluvproject.domain.chat.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
