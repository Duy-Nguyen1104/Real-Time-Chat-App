package com.chat_app.web_socket_chat_application.domain.repository;

import com.chat_app.web_socket_chat_application.domain.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByConversationId(String conversationId);
    List<ChatMessage> findBySenderIdAndReceiverId(String senderId, String receiverId);
    List<ChatMessage> findByConversationIdAndReceiverIdAndReadFalse(String conversationId, String receiverId);
}