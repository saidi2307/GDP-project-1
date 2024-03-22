package com.team01.wholeSaleSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team01.wholeSaleSystem.entity.ChatRoom;
import com.team01.wholeSaleSystem.entity.User;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(User senderId, User recipientId);
}
