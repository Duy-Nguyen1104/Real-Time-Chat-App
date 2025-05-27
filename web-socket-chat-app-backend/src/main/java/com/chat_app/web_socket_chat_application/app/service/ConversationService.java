package com.chat_app.web_socket_chat_application.app.service;

import com.chat_app.web_socket_chat_application.api.dto.ConversationDTO;
import com.chat_app.web_socket_chat_application.app.exceptions.AppException;
import com.chat_app.web_socket_chat_application.app.exceptions.ExceptionCode;
import com.chat_app.web_socket_chat_application.domain.entity.Conversation;
import com.chat_app.web_socket_chat_application.domain.entity.User;
import com.chat_app.web_socket_chat_application.domain.repository.ConversationRepository;
import com.chat_app.web_socket_chat_application.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ConversationService {
    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    public Conversation createOrGetConversation(String senderId, String receiverId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_EXISTED));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_EXISTED));

        // Try to find the conversation where current user is sender
        Optional<Conversation> conversation = conversationRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId());

        // Also check if conversation exists where current user is receiver
        if (conversation.isEmpty()) {
            conversation = conversationRepository.findBySenderIdAndReceiverId(receiver.getId(), sender.getId());
        }

        return conversation.orElseGet(() -> {
            Conversation newConversation = new Conversation();
            newConversation.setSenderId(senderId);
            newConversation.setReceiverId(receiverId);
            newConversation.setName(receiver.getName()); // Keep this for backward compatibility
            newConversation.setLastMessage(""); // Empty last message
            newConversation.setLastMessageTime(null);
            newConversation.setUnreadCount(0);
            newConversation.setOnline(false); // Default to offline
            newConversation.setCategory("all"); // Default category
            return conversationRepository.save(newConversation);
        });
    }

    public List<Conversation> getAllConversations() {
        return conversationRepository.findAll();
    }

    public Conversation getConversation(String id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new AppException(ExceptionCode.CHATROOM_NOT_EXISTED));
    }

    public void markAsRead(String id) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new AppException(ExceptionCode.CHATROOM_NOT_EXISTED));
        conversation.setUnreadCount(0);
        conversationRepository.save(conversation);
    }

    public void deleteConversation(String id) {
        if (!conversationRepository.existsById(id)) {
            throw new AppException(ExceptionCode.CHATROOM_NOT_EXISTED);
        }
        conversationRepository.deleteById(id);
    }

    public void updateLastMessage(String conversationId, String message, String timestamp) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ExceptionCode.CHATROOM_NOT_EXISTED));
        conversation.setLastMessage(message);
        conversation.setLastMessageTime(timestamp);
        conversation.setUnreadCount(conversation.getUnreadCount() + 1);
        conversationRepository.save(conversation);
    }

    public List<Conversation> getConversationsBySenderId(String senderId) {
        userRepository.findById(senderId)
                .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_EXISTED));

        return conversationRepository.findBySenderId(senderId);
    }

    public List<ConversationDTO> getUserConversations(String userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_EXISTED));

        List<Conversation> conversations = conversationRepository.findBySenderIdOrReceiverId(userId, userId);

        Collections.sort(conversations, (c1, c2) -> {
            // If no last message, put at the end
            if (c1.getLastMessageTime() == null) return 1;
            if (c2.getLastMessageTime() == null) return -1;

            // Compare timestamps in reverse order (newest first)
            return c2.getLastMessageTime().compareTo(c1.getLastMessageTime());
        });

        return conversations.stream().map(conversation -> {
            String displayName;
            String otherUserId;

            if (conversation.getSenderId().equals(userId)) {
                otherUserId = conversation.getReceiverId();
            } else {
                otherUserId = conversation.getSenderId();
            }

            User otherUser = userRepository.findById(otherUserId)
                    .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_EXISTED));
            displayName = otherUser.getName();

            return ConversationDTO.builder()
                    .id(conversation.getId())
                    .displayName(displayName)
                    .lastMessage(conversation.getLastMessage())
                    .lastMessageTime(conversation.getLastMessageTime())
                    .unreadCount(conversation.getUnreadCount())
                    .online(conversation.isOnline())
                    .avatarColor(conversation.getAvatarColor())
                    .category(conversation.getCategory())
                    .senderId(conversation.getSenderId())
                    .receiverId(conversation.getReceiverId())
                    .chatId(conversation.getChatId())
                    .build();
        }).collect(Collectors.toList());
    }
}
