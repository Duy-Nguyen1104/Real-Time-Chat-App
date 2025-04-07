package com.chat_app.web_socket_chat_application.api.controller;

import com.chat_app.web_socket_chat_application.api.dto.ConversationDTO;
import com.chat_app.web_socket_chat_application.api.dto.CreateConversationDTO;
import com.chat_app.web_socket_chat_application.api.response.ApiResponse;
import com.chat_app.web_socket_chat_application.api.response.SuccessResponse;
import com.chat_app.web_socket_chat_application.app.service.ConversationService;
import com.chat_app.web_socket_chat_application.domain.entity.Conversation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
@Slf4j
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping
    public ApiResponse<List<Conversation>> getAllConversations() {
        log.info("Getting all conversations");
        return new SuccessResponse<>(conversationService.getAllConversations());
    }

    @GetMapping(("/user/{userId}"))
    public ApiResponse<List<ConversationDTO>> getUserConversations(@PathVariable String userId) {
        log.info("Getting conversations for user with id: {}", userId);
        return new SuccessResponse<>(conversationService.getUserConversations(userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<Conversation> getConversation(@PathVariable String id) {
        log.info("Getting conversation with id: {}", id);
        return new ApiResponse<>(conversationService.getConversation(id));
    }

    @PostMapping()
    public ApiResponse<Conversation> createOrGetConversation(@RequestBody CreateConversationDTO conversationDTO) {
        log.info("Creating conversation between: {} and {}", conversationDTO.getSenderId(), conversationDTO.getReceiverId());
        Conversation conversation = conversationService.createOrGetConversation(conversationDTO.getSenderId(), conversationDTO.getReceiverId());
        return new SuccessResponse<>(conversation);
    }

    @PostMapping("/{id}/read")
    public ApiResponse<?> markAsRead(@PathVariable String id) {
        log.info("Marking conversation {} as read", id);
        conversationService.markAsRead(id);
        return new SuccessResponse<>();
    }
}
