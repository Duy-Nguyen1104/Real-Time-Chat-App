package com.chat_app.web_socket_chat_application.domain.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "users")
public class User {
    @Id
    private String id;

    private String name;
    private String phoneNumber;
    private String password;
    private String status;
}
