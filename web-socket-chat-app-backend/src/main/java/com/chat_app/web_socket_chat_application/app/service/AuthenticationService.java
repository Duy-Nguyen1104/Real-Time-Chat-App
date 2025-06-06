package com.chat_app.web_socket_chat_application.app.service;

import com.chat_app.web_socket_chat_application.api.dto.AuthenticationDTO;
import com.chat_app.web_socket_chat_application.api.dto.ResetPasswordDTO;
import com.chat_app.web_socket_chat_application.api.dto.UserDTO;
import com.chat_app.web_socket_chat_application.api.response.AuthenticationResponse;
import com.chat_app.web_socket_chat_application.app.exceptions.AppException;
import com.chat_app.web_socket_chat_application.app.exceptions.ExceptionCode;
import com.chat_app.web_socket_chat_application.config.JwtUtil;
import com.chat_app.web_socket_chat_application.domain.entity.Conversation;
import com.chat_app.web_socket_chat_application.domain.entity.User;
import com.chat_app.web_socket_chat_application.domain.repository.ConversationRepository;
import com.chat_app.web_socket_chat_application.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthenticationResponse login(AuthenticationDTO authenticationDTO) {
        User user = userRepository.findByPhoneNumber(authenticationDTO.getPhoneNumber());
        List<Conversation> conversations = conversationRepository.findBySenderIdOrReceiverId(user.getId(), user.getId());

        if (!passwordEncoder.matches(authenticationDTO.getPassword(), user.getPassword())) {
            throw new AppException(ExceptionCode.INVALID_PASSWORD);
        }

        user.setStatus("online");
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getPhoneNumber());

        return new AuthenticationResponse(token, user.getId(), user.getName(), user.getStatus());
    }

    public AuthenticationResponse resetPassword(ResetPasswordDTO request) {
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber());
        if (user == null) {
            throw new AppException(ExceptionCode.USER_NOT_EXISTED);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getPhoneNumber());
        return new AuthenticationResponse(token, user.getId(), user.getName(), user.getStatus());
    }

    public void register(UserDTO userDTO) {
        if (userRepository.findByPhoneNumber(userDTO.getPhoneNumber()) != null) {
            throw new AppException(ExceptionCode.USER_EXISTED);
        }

        User user = new User();

        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setName(userDTO.getName());
//        user.setStatus("online");

        userRepository.save(user);
    }

}
