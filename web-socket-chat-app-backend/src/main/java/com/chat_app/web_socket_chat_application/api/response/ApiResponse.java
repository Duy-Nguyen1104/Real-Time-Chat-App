package com.chat_app.web_socket_chat_application.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApiResponse<T> {
    private final String message;
    private final int code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    public ApiResponse(String message, int code, T data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public ApiResponse(String message, int code) {
        this(message, code, null);
    }
    public ApiResponse(T data) {
        this("success", 200, data);
    }

}
