package com.chat_app.web_socket_chat_application.app.service;

import com.chat_app.web_socket_chat_application.domain.entity.User;
import com.chat_app.web_socket_chat_application.domain.repository.UserRepository;
import com.chat_app.web_socket_chat_application.app.exceptions.AppException;
import com.chat_app.web_socket_chat_application.app.exceptions.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> searchUsers(String query, String currentUserId) {
        Set<User> distinctUsers = new HashSet<>();

        // Try searching by phone number
        User userByPhone = userRepository.findByPhoneNumber(query);
        if (userByPhone != null && !userByPhone.getId().equals(currentUserId)) {
            distinctUsers.add(userByPhone);
        }

        // Search by name (containing query), case-insensitive
        List<User> usersByName = userRepository.findByNameContainingIgnoreCase(query);
        usersByName.stream()
                .filter(user -> !user.getId().equals(currentUserId))
                .forEach(distinctUsers::add);

        return new ArrayList<>(distinctUsers);
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_EXISTED));
    }

    public User findByPhoneNumber(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber);
        if (user == null) {
            throw new AppException(ExceptionCode.USER_NOT_EXISTED);
        }
        return user;
    }

    public User updateUserStatus(String userId, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_EXISTED));
        user.setStatus(status);
        return userRepository.save(user);
    }

    public void disconnectUser(String userId) {
        User storedUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_EXISTED));
        storedUser.setStatus("offline");
        userRepository.save(storedUser);
    }
}