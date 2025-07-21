package com.whispeer.chat.chatroom.repository;

import com.whispeer.chat.chatroom.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomEntityRepository extends JpaRepository<ChatRoomEntity, Long> {
}