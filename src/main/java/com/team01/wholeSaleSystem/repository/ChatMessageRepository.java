package com.team01.wholeSaleSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team01.wholeSaleSystem.entity.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatId(String chatId);
}
