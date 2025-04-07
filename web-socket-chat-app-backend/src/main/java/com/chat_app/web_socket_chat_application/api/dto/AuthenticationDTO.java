package com.chat_app.web_socket_chat_application.api.dto;

import lombok.Data;

@Data
public class AuthenticationDTO {
    private String phoneNumber;
    private String password;
}
