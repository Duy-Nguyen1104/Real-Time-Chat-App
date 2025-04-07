package com.chat_app.web_socket_chat_application.app.service;

import com.chat_app.web_socket_chat_application.domain.entity.ChatMessage;
import com.chat_app.web_socket_chat_application.domain.entity.Conversation;
import com.chat_app.web_socket_chat_application.domain.entity.User;
import com.chat_app.web_socket_chat_application.domain.repository.ChatMessageRepository;
import com.chat_app.web_socket_chat_application.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
    @Autowired
    private final ChatMessageRepository chatMessageRepository;
    @Autowired
    private final ConversationService conversationService;
    @Autowired
    private final UserRepository userRepository;

    public ChatMessage save(ChatMessage chatMessage) {
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(new Date().toString());
        }
        Conversation conversation = conversationService.createOrGetConversation(
                chatMessage.getSenderId(), chatMessage.getReceiverId());

        chatMessage.setConversationId(conversation.getId());
        chatMessage.setRead(false);
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        conversationService.updateLastMessage(
                conversation.getId(),
                chatMessage.getContent(),
                chatMessage.getTimestamp());

        log.info("Chat message saved: {}", savedMessage);
        return savedMessage;
    }

    public List<ChatMessage> findChatMessages(String senderId, String receiverId) {
        Conversation conversation = conversationService.createOrGetConversation(senderId, receiverId);

        List<ChatMessage> messages = chatMessageRepository.findByConversationId(conversation.getId());

        return messages.stream().map(message -> {
            User sender = userRepository.findById(message.getSenderId()).orElse(null);
            if (sender != null) {
                message.setSender(new ChatMessage.SenderInfo(sender.getId(), sender.getName()));
            }
            return message;
        }).collect(Collectors.toList());
    }

    public void deleteMessage(String id) {
        if (!chatMessageRepository.existsById(id)) {
            throw new IllegalArgumentException("Message with ID " + id + " does not exist.");
        }
        chatMessageRepository.deleteById(id);
        log.info("Deleted message with ID: {}", id);
    }

    public void markMessageAsRead(String messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
        message.setRead(true);
        chatMessageRepository.save(message);
    }
}