package com.example.luvisluvproject.domain.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.luvisluvproject.domain.chat.entity.MemberChatRoom;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {
	MemberChatRoom findByMemberIdAndChatRoomId(Long memberId, Long chatRoomId);

	Slice<MemberChatRoom> findAllByMemberIdAndDeletedFalse(Long id, Pageable pageable);

	Boolean existsByMemberIdAndChatRoomId(Long memberId, Long chatRoomId);
}
