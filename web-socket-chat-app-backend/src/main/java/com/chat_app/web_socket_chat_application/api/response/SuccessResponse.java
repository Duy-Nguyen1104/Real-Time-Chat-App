package com.chat_app.web_socket_chat_application.api.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SuccessResponse<T> extends ApiResponse<T>{
    public SuccessResponse(T data) {
        super("Success", 200, data);
    }

    public SuccessResponse() {
        super("Success", 200, null);
    }

    public SuccessResponse(T data, HttpStatus status) {
        super("Success", status.value(), data);
    }

}
