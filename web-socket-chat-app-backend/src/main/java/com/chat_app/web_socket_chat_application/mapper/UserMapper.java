package com.chat_app.web_socket_chat_application.mapper;

import com.chat_app.web_socket_chat_application.api.dto.UserDTO;
import com.chat_app.web_socket_chat_application.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserDTO userDTO);
}
