package com.chat_app.web_socket_chat_application.api.controller;

import com.chat_app.web_socket_chat_application.api.response.ApiResponse;
import com.chat_app.web_socket_chat_application.api.response.SuccessResponse;
import com.chat_app.web_socket_chat_application.app.service.ChatMessageService;
import com.chat_app.web_socket_chat_application.domain.entity.ChatMessage;
import com.chat_app.web_socket_chat_application.util.TimestampUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

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

        // Set timestamp if not set using TimestampUtil
        chatMessage.setTimestamp(TimestampUtil.ensureTimestamp(chatMessage.getTimestamp()));
        
        ChatMessage savedMessage = chatMessageService.save(chatMessage);

        // Send to the receiver's direct queue
        String receiverQueue = "/queue/user." + chatMessage.getReceiverId() + ".messages";
        log.info("Sending to receiver queue: {}", receiverQueue);
        simpMessagingTemplate.convertAndSend(receiverQueue, savedMessage);

        // Also send to the sender's direct queue for real-time updates across devices
        String senderQueue = "/queue/user." + chatMessage.getSenderId() + ".messages";
        log.info("Sending to sender queue: {}", senderQueue);
        simpMessagingTemplate.convertAndSend(senderQueue, savedMessage);
    }

    @GetMapping("/messages/{senderId}/{receiverId}")
    public ApiResponse<List<ChatMessage>> findChatMessages(
            @PathVariable String senderId,
            @PathVariable String receiverId
    ) {
        log.info("Finding chat messages between {} and {}", senderId, receiverId);
        List<ChatMessage> messages = chatMessageService.findChatMessagesBetweenUsers(senderId, receiverId);
        return new SuccessResponse<>(messages);
    }

    @PostMapping("/messages")
    public ApiResponse<ChatMessage> sendMessage(@RequestBody ChatMessage chatMessage) {
        log.info("Sending message via REST: {}", chatMessage);

        // Set timestamp if not set using TimestampUtil
        chatMessage.setTimestamp(TimestampUtil.ensureTimestamp(chatMessage.getTimestamp()));

        // Save the message
        ChatMessage savedMessage = chatMessageService.save(chatMessage);

        // Send to the receiver's direct queue
        String receiverQueue = "/queue/user." + chatMessage.getReceiverId() + ".messages";
        log.info("Sending to receiver queue via REST: {}", receiverQueue);
        simpMessagingTemplate.convertAndSend(receiverQueue, savedMessage);

        // Also send to the sender's direct queue for real-time updates across devices
        String senderQueue = "/queue/user." + chatMessage.getSenderId() + ".messages";
        log.info("Sending to sender queue via REST: {}", senderQueue);
        simpMessagingTemplate.convertAndSend(senderQueue, savedMessage);

        return new SuccessResponse<>(savedMessage);
    }
}