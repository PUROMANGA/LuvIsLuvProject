package com.example.luvisluvproject.domain.chat.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.luvisluvproject.domain.chat.entity.Message;

public interface MessageRepository extends MongoRepository<Message, Long> {

	Slice<Message> findAllByChatRoomId(Long chatId, Pageable pageable);
	Optional<Message> findByChatRoomId(Long chatRoomId);
}
