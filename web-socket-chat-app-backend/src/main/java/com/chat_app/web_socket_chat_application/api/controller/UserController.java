package com.chat_app.web_socket_chat_application.api.controller;

import com.chat_app.web_socket_chat_application.api.dto.UserResponseDTO;
import com.chat_app.web_socket_chat_application.api.response.ApiResponse;
import com.chat_app.web_socket_chat_application.api.response.SuccessResponse;
import com.chat_app.web_socket_chat_application.app.service.UserService;
import com.chat_app.web_socket_chat_application.domain.entity.User;
import com.chat_app.web_socket_chat_application.mapper.UserMapper;
import com.chat_app.web_socket_chat_application.domain.repository.UserRepository; // Added for current user lookup
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
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

    // WebSocket related user status updates (can be in a separate controller if preferred)
    // These are typically not REST endpoints but message mappings.
    // For simplicity, keeping them here but they are not invoked via /api/users HTTP calls.

    @MessageMapping("/user.connect") // Renamed from addUser for clarity
    @SendTo("/topic/users") // Broadcast to a general topic
    public UserResponseDTO handleUserConnect(@Payload String userId) { // Assuming client sends userId on connect
        log.info("User connected with ID: {}", userId);
        User connectedUser = userService.updateUserStatus(userId, "online");
        return userMapper.toUserResponseDTO(connectedUser);
    }

    @MessageMapping("/user.disconnect") // Renamed from disconnectUser
    @SendTo("/topic/users") // Broadcast to a general topic
    public UserResponseDTO handleUserDisconnect(@Payload String userId) { // Assuming client sends userId on disconnect
        log.info("User disconnected with ID: {}", userId);
        // The service method sets status to "offline" and saves
        userService.disconnectUser(userId);
        // For the broadcast, create a DTO representing the disconnected state
        User user = new User(); // Create a temporary user object for DTO mapping
        user.setId(userId);
        // Fetch user details to get name if needed for the DTO, or just send ID and offline status
        User disconnectedUser = userService.getUserById(userId); // to get name
        disconnectedUser.setStatus("offline");
        return userMapper.toUserResponseDTO(disconnectedUser);
    }
}