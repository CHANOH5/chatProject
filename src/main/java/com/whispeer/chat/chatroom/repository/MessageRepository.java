package com.whispeer.chat.chatroom.repository;

import com.whispeer.chat.chatroom.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageEntityRepository extends JpaRepository<MessageEntity, Long> {
}