package com.chat_app.web_socket_chat_application.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "conversations")
public class Conversation {
    @Id
    private String id;

    private String name;
    private String lastMessage;
    private String lastMessageTime;
    private int unreadCount;
    private boolean online;
    private String avatarColor;
    private String category;

    private String senderId;
    private String receiverId;

    public String getChatId() {
        return senderId + "_" + receiverId;
    }
}
