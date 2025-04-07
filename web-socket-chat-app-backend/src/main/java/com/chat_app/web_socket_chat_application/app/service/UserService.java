package com.chat_app.web_socket_chat_application.app.service;

import com.chat_app.web_socket_chat_application.api.dto.UserDTO;
import com.chat_app.web_socket_chat_application.app.exceptions.AppException;
import com.chat_app.web_socket_chat_application.app.exceptions.ExceptionCode;
import com.chat_app.web_socket_chat_application.domain.repository.UserRepository;
import com.chat_app.web_socket_chat_application.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        user.setStatus("online");
        return userRepository.save(user);
    }

    public void disconnectUser(User user) {
        User storedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_EXISTED));
        if (storedUser != null) {
            storedUser.setStatus("offline");
            userRepository.save(storedUser);
        }
    }

    public List<User> getOnlineUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> Objects.equals(user.getStatus(), "online"))
                .collect(Collectors.toList());
    }
}
