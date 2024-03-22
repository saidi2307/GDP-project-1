package com.team01.wholeSaleSystem.liveChat.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.team01.wholeSaleSystem.entity.ChatMessage;
import com.team01.wholeSaleSystem.entity.User;
import com.team01.wholeSaleSystem.liveChat.chatroom.ChatRoomService;
import com.team01.wholeSaleSystem.repository.ChatMessageRepository;
import com.team01.wholeSaleSystem.repository.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;

    public ChatMessage save(ChatMessageDto chatMessageDto) {
        User sender = userRepository.findByUserName(chatMessageDto.getSenderId());
        User recipient = userRepository.findByUserName(chatMessageDto.getRecipientId());

        var chatId = chatRoomService
                .getChatRoomId(sender, recipient, true)
                .orElseThrow(); // You can create your own dedicated exception

        ChatMessage chatMessage = ChatMessage.builder()
                .chatId(chatId)
                .senderId(sender)
                .recipientId(recipient)
                .content(chatMessageDto.getContent())
                .timestamp(new Timestamp(Instant.now().toEpochMilli()))
                .build();
        repository.save(chatMessage);
        return chatMessage;
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatRoomId(userRepository.findByUserName(senderId),
                userRepository.findByUserName(recipientId), true);
        return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
    }
}
