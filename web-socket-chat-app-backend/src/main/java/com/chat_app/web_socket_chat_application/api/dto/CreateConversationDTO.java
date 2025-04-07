package com.chat_app.web_socket_chat_application.api.dto;

import lombok.*;

@Data
public class CreateConversationDTO {
    private String senderId;
    private String receiverId;
}
