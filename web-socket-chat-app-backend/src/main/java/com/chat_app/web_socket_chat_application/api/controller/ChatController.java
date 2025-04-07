package com.chat_app.web_socket_chat_application.api.controller;

import com.chat_app.web_socket_chat_application.api.response.ApiResponse;
import com.chat_app.web_socket_chat_application.api.response.SuccessResponse;
import com.chat_app.web_socket_chat_application.app.service.ChatMessageService;
import com.chat_app.web_socket_chat_application.domain.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        log.info("Processing chat message: {}", chatMessage);

        // Set timestamp if not set
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(new Date().toString());
        }
        ChatMessage savedMessage = chatMessageService.save(chatMessage);
        // Send to the receiver's queue
        simpMessagingTemplate.convertAndSendToUser(
                chatMessage.getReceiverId(),
                "/queue/messages",
                savedMessage
        );
    }

    @GetMapping("/messages/{senderId}/{receiverId}")
    public ApiResponse<List<ChatMessage>> findChatMessages(
            @PathVariable String senderId,
            @PathVariable String receiverId
    ) {
        log.info("Finding chat messages between {} and {}", senderId, receiverId);
        List<ChatMessage> messages = chatMessageService.findChatMessages(senderId, receiverId);
        return new SuccessResponse<>(messages);
    }

    @PostMapping("/messages")
    public ApiResponse<ChatMessage> sendMessage(@RequestBody ChatMessage chatMessage) {
        log.info("Sending message via REST: {}", chatMessage);

        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(new Date().toString());
        }
        // Save the message
        ChatMessage savedMessage = chatMessageService.save(chatMessage);

        return new SuccessResponse<>(savedMessage);
    }
}