package com.chat_app.web_socket_chat_application.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {
    private String id;
    private String name;
    private String phoneNumber;
    private String status;
}
