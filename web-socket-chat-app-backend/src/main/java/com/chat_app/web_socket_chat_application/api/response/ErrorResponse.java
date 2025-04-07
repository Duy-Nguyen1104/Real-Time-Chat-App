package com.chat_app.web_socket_chat_application.api.response;

import org.springframework.http.HttpStatus;

public class ErrorResponse<T> extends ApiResponse<T> {
    public ErrorResponse() {
        super("Error", HttpStatus.BAD_REQUEST.value());
    }

    public ErrorResponse(String message) {
        super(message, HttpStatus.BAD_REQUEST.value());
    }

    public ErrorResponse(String message, T data) {
        super( message, HttpStatus.BAD_REQUEST.value(), data);
    }
}
