package com.chat_app.web_socket_chat_application.api.controller;

import com.chat_app.web_socket_chat_application.api.dto.AuthenticationDTO;
import com.chat_app.web_socket_chat_application.api.dto.UserDTO;
import com.chat_app.web_socket_chat_application.api.response.ApiResponse;
import com.chat_app.web_socket_chat_application.api.response.AuthenticationResponse;
import com.chat_app.web_socket_chat_application.api.response.SuccessResponse;
import com.chat_app.web_socket_chat_application.app.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationDTO authenticationDTO) {
        log.info("Login with phone number: {}", authenticationDTO.getPhoneNumber());
        AuthenticationResponse authenticationResponse = authenticationService.login(authenticationDTO);
        return new SuccessResponse<>(authenticationResponse);
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody UserDTO userDTO) {
        log.info("Register with phone number: {}", userDTO.getPhoneNumber());
        authenticationService.register(userDTO);
        return new SuccessResponse<>(HttpStatus.CREATED);
    }
}
