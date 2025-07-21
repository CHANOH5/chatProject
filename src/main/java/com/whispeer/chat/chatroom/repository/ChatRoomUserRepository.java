package com.whispeer.chat.chatroom.repository;

import com.whispeer.chat.chatroom.entity.ChatRoomUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomUserEntityRepository extends JpaRepository<ChatRoomUserEntity, Long> {
}