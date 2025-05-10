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

import java.text.SimpleDateFormat;
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            chatMessage.setTimestamp(dateFormat.format(new Date()));
        }

        Conversation conversation = conversationService.createOrGetConversation(
                chatMessage.getSenderId(), chatMessage.getReceiverId());

        chatMessage.setConversationId(conversation.getId());
        chatMessage.setRead(false);

        // Set sender information
        User sender = userRepository.findById(chatMessage.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        chatMessage.setSender(new ChatMessage.SenderInfo(sender.getId(), sender.getName()));

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        // Format timestamp for displaying in conversation list
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String displayTime = timeFormat.format(new Date());

        conversationService.updateLastMessage(
                conversation.getId(),
                chatMessage.getContent(),
                displayTime);

        log.info("Chat message saved: {}", savedMessage);
        return savedMessage;
    }

    public List<ChatMessage> findChatMessages(String senderId, String receiverId) {
        Conversation conversation = conversationService.createOrGetConversation(senderId, receiverId);
        return findMessagesByConversationId(conversation.getId());
    }

    public List<ChatMessage> findChatMessagesBetweenUsers(String userId1, String userId2) {
        // Try to find conversation in both directions
        Conversation conversation = conversationService.createOrGetConversation(userId1, userId2);
        return findMessagesByConversationId(conversation.getId());
    }

    private List<ChatMessage> findMessagesByConversationId(String conversationId) {
        List<ChatMessage> messages = chatMessageRepository.findByConversationId(conversationId);

        return messages.stream().map(message -> {
            // Ensure sender info is included
            if (message.getSender() == null) {
                User sender = userRepository.findById(message.getSenderId()).orElse(null);
                if (sender != null) {
                    message.setSender(new ChatMessage.SenderInfo(sender.getId(), sender.getName()));
                }
            }
            return message;
        }).collect(Collectors.toList());
    }

    public void markMessagesAsRead(String conversationId, String receiverId) {
        List<ChatMessage> unreadMessages = chatMessageRepository
                .findByConversationIdAndReceiverIdAndReadFalse(conversationId, receiverId);

        unreadMessages.forEach(message -> {
            message.setRead(true);
            chatMessageRepository.save(message);
        });

        // Update conversation unread count
        conversationService.markAsRead(conversationId);
    }
}