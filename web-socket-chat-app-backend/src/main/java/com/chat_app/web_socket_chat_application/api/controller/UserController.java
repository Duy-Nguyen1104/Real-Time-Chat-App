package com.chat_app.web_socket_chat_application.api.controller;

import com.chat_app.web_socket_chat_application.api.response.ApiResponse;
import com.chat_app.web_socket_chat_application.api.response.SuccessResponse;
import com.chat_app.web_socket_chat_application.app.service.UserService;
import com.chat_app.web_socket_chat_application.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @MessageMapping("/user.addUser")
    @SendTo("/user/topic")
    //notify the other users that there is a new user connected. send to an automatic created queue
    public ApiResponse<User> saveUser(@Payload User user) {
        User result = userService.saveUser(user);
        return new ApiResponse<>("User saved successfully", 200, result);
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/user/topic") //notify to the same queue has disconnected
    public ApiResponse<User> disconnectUser(@Payload User user) {
        userService.disconnectUser(user);
        return new ApiResponse<>("User disconnected successfully", 200);
    }

    @GetMapping("/users")
    public ApiResponse<List<User>> getOnlineUsers() {
        List<User> userList = userService.getOnlineUsers();
        return new SuccessResponse<>(userList);
    }


}
