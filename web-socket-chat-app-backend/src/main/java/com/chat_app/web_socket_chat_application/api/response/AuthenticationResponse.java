package com.chat_app.web_socket_chat_application.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String id;
    private String name;
    private String status;
}
