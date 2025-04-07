package com.chat_app.web_socket_chat_application.domain.repository;

import com.chat_app.web_socket_chat_application.domain.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<User, String> {
    User findByName(String name);
    User findByPhoneNumber(String phoneNumber);
}
