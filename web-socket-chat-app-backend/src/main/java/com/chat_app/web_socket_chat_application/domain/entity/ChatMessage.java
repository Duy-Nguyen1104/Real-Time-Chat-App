package com.chat_app.web_socket_chat_application.domain.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "chat_messages")
public class ChatMessage {
    @Id
    private String id;
    private String conversationId;  
    private String senderId;        
    private String receiverId;      
    private String content;         
    private String timestamp;         
    private boolean read; 

    @Transient
    private SenderInfo sender;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SenderInfo {
        private String id;
        private String name;
    }
}
