package com.chat_app.web_socket_chat_application.app.exceptions;

import lombok.Getter;

@Getter
public enum ExceptionCode {
    USER_EXISTED(1002, "User already exists"),
    INVALID_USERNAME(1003, "Invalid username"),
    INVALID_PASSWORD(1004, "Invalid password"),
    USER_NOT_EXISTED(1005, "User does not exist"),
    UNAUTHORIZED(401, "Unauthorized"),

    CHATROOM_NOT_EXISTED(402, "Chatroom does not exist"),
    ;
    private int code;
    private String message;
    ExceptionCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
