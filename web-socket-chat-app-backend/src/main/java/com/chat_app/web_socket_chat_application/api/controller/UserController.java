package com.chat_app.web_socket_chat_application.api.controller;

import com.chat_app.web_socket_chat_application.api.dto.UserResponseDTO;
import com.chat_app.web_socket_chat_application.api.response.ApiResponse;
import com.chat_app.web_socket_chat_application.api.response.SuccessResponse;
import com.chat_app.web_socket_chat_application.app.service.UserService;
import com.chat_app.web_socket_chat_application.domain.entity.User;
import com.chat_app.web_socket_chat_application.domain.repository.UserRepository;
import com.chat_app.web_socket_chat_application.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository; // Autowire UserRepository for current user lookup

    @GetMapping
    public ApiResponse<List<UserResponseDTO>> getAllUsers() {
        log.info("Fetching all users");
        List<User> users = userService.findAllUsers();
        return new SuccessResponse<>(userMapper.toUserResponseDTOList(users));
    }

    @GetMapping("/search")
    public ApiResponse<List<UserResponseDTO>> searchUsers(@RequestParam String query) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName(); // This is the phone number from JWT subject

        User currentUser = userRepository.findByPhoneNumber(currentPrincipalName);
        if (currentUser == null) {
            return new ApiResponse<>("Error: Current user not found based on token.", 404, null);
        }
        String currentUserId = currentUser.getId();

        log.info("Searching for users with query: {} (excluding user ID: {})", query, currentUserId);
        List<User> users = userService.searchUsers(query, currentUserId);
        return new SuccessResponse<>(userMapper.toUserResponseDTOList(users));
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponseDTO> getUserById(@PathVariable String userId) {
        log.info("Fetching user by ID: {}", userId);
        User user = userService.getUserById(userId);
        return new SuccessResponse<>(userMapper.toUserResponseDTO(user));
    }
}