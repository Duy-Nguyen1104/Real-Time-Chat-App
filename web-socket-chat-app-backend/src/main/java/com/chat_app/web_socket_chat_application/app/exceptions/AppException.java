package com.chat_app.web_socket_chat_application.app.exceptions;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private int code;
    private String message;

    public AppException(String message) {
        super(message);
    }

    public AppException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMessage();
    }

}
