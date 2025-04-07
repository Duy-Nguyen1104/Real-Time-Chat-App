package com.chat_app.web_socket_chat_application.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationDTO {
    private String id;
    private String displayName;
    private String lastMessage;
    private String lastMessageTime;
    private int unreadCount;
    private boolean online;
    private String avatarColor;
    private String category;
    private String senderId;
    private String receiverId;
    private String chatId;
}
