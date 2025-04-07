package com.chat_app.web_socket_chat_application.app.service;

import com.chat_app.web_socket_chat_application.api.dto.AuthenticationDTO;
import com.chat_app.web_socket_chat_application.api.dto.UserDTO;
import com.chat_app.web_socket_chat_application.api.response.AuthenticationResponse;
import com.chat_app.web_socket_chat_application.app.exceptions.AppException;
import com.chat_app.web_socket_chat_application.app.exceptions.ExceptionCode;
import com.chat_app.web_socket_chat_application.config.JwtUtil;
import com.chat_app.web_socket_chat_application.domain.entity.User;
import com.chat_app.web_socket_chat_application.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthenticationResponse login(AuthenticationDTO authenticationDTO) {
        User user = userRepository.findByPhoneNumber(authenticationDTO.getPhoneNumber());

        if (user == null) {
            throw new AppException(ExceptionCode.USER_NOT_EXISTED);
        }

        if (!passwordEncoder.matches(authenticationDTO.getPassword(), user.getPassword())) {
            throw new AppException(ExceptionCode.INVALID_PASSWORD);
        }

        user.setStatus("online");
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
