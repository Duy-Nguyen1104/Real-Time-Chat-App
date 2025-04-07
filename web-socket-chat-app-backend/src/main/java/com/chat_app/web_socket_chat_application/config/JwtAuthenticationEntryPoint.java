package com.chat_app.web_socket_chat_application.config;

import com.chat_app.web_socket_chat_application.api.response.ApiResponse;
import com.chat_app.web_socket_chat_application.app.exceptions.ExceptionCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        ExceptionCode exceptionCode = ExceptionCode.UNAUTHORIZED;

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Object> apiResponse = ApiResponse.<Object>builder()
                .code(exceptionCode.getCode())
                .message(exceptionCode.getMessage())
                .data(null)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        //Put the ApiResponse in the response object
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        //Commit response
        response.flushBuffer();
    }
}

