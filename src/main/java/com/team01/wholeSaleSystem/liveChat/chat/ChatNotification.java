package com.team01.wholeSaleSystem.liveChat.chat;

import com.team01.wholeSaleSystem.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatNotification {
    private Long id;
    private User senderId;
    private User recipientId;
    private String content;
}
