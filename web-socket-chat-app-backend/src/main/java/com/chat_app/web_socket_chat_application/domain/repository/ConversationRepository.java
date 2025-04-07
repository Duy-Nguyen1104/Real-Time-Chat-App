package com.chat_app.web_socket_chat_application.domain.repository;

import com.chat_app.web_socket_chat_application.domain.entity.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends MongoRepository<Conversation, String> {
    Optional<Conversation> findBySenderIdAndReceiverId(String senderId, String receiverId);
    List<Conversation> findBySenderId(String senderId);
    List<Conversation> findByReceiverId(String receiverId);
    List<Conversation> findBySenderIdOrReceiverId(String senderId, String receiverId);
}