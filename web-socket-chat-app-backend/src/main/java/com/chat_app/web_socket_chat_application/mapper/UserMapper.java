package com.chat_app.web_socket_chat_application.mapper;

import com.chat_app.web_socket_chat_application.api.dto.UserDTO;
import com.chat_app.web_socket_chat_application.api.dto.UserResponseDTO;
import com.chat_app.web_socket_chat_application.domain.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserDTO userDTO);

    UserResponseDTO toUserResponseDTO(User user);
    List<UserResponseDTO> toUserResponseDTOList(List<User> users);
}
