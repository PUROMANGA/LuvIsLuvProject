package com.example.luvisluvproject.domain.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luvisluvproject.domain.chat.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

	Slice<Message> findAllByChatRoomId(Long chatId, Pageable pageable);
}
