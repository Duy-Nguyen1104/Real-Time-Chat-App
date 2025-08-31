package com.chat_app.web_socket_chat_application.api.controller;

import com.chat_app.web_socket_chat_application.api.response.ApiResponse;
import com.chat_app.web_socket_chat_application.api.response.SuccessResponse;
import com.chat_app.web_socket_chat_application.app.service.ChatMessageService;
import com.chat_app.web_socket_chat_application.domain.entity.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private ChatMessageService chatMessageService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private ChatController chatController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void processMessage_shouldSaveMessageAndSendToReceiver() {
        // Arrange
        ChatMessage chatMessage = createSampleChatMessage();
        ChatMessage savedMessage = createSavedChatMessage();

        when(chatMessageService.save(any(ChatMessage.class))).thenReturn(savedMessage);

        // Act
        chatController.processMessage(chatMessage);

        // Assert
        verify(chatMessageService).save(any(ChatMessage.class));
        // Verify message is sent to both receiver and sender
        verify(simpMessagingTemplate).convertAndSendToUser(
                eq("user2"),
                eq("/queue/messages"),
                eq(savedMessage)
        );
        verify(simpMessagingTemplate).convertAndSendToUser(
                eq("user1"),
                eq("/queue/messages"),
                eq(savedMessage)
        );
    }

    @Test
    void processMessage_shouldSetTimestampIfNotProvided() {
        // Arrange
        ChatMessage chatMessage = createSampleChatMessage();
        chatMessage.setTimestamp(null);
        ChatMessage savedMessage = createSavedChatMessage();

        when(chatMessageService.save(any(ChatMessage.class))).thenReturn(savedMessage);

        // Act
        chatController.processMessage(chatMessage);

        // Assert
        verify(chatMessageService).save(argThat(msg -> msg.getTimestamp() != null));
        // Verify message is sent to both receiver and sender (2 calls total)
        verify(simpMessagingTemplate, times(2)).convertAndSendToUser(anyString(), anyString(), any());
        // Verify specific calls to receiver and sender
        verify(simpMessagingTemplate).convertAndSendToUser(eq("user2"), eq("/queue/messages"), eq(savedMessage));
        verify(simpMessagingTemplate).convertAndSendToUser(eq("user1"), eq("/queue/messages"), eq(savedMessage));
    }

    @Test
    void findChatMessages_shouldReturnMessagesBetweenUsers() throws Exception {
        // Arrange
        String senderId = "user1";
        String receiverId = "user2";
        List<ChatMessage> messages = Arrays.asList(createSavedChatMessage());

        when(chatMessageService.findChatMessagesBetweenUsers(senderId, receiverId))
                .thenReturn(messages);

        // Act & Assert
        mockMvc.perform(get("/messages/{senderId}/{receiverId}", senderId, receiverId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].id").value("msg1"))
                .andExpect(jsonPath("$.data[0].content").value("Hello World"));

        verify(chatMessageService).findChatMessagesBetweenUsers(senderId, receiverId);
    }

    @Test
    void sendMessage_shouldSaveMessageAndSendNotification() throws Exception {
        // Arrange
        ChatMessage chatMessage = createSampleChatMessage();
        ChatMessage savedMessage = createSavedChatMessage();

        when(chatMessageService.save(any(ChatMessage.class))).thenReturn(savedMessage);

        // Act & Assert
        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatMessage)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value("msg1"))
                .andExpect(jsonPath("$.data.content").value("Hello World"));

        verify(chatMessageService).save(any(ChatMessage.class));
        // Verify message is sent to both receiver and sender
        verify(simpMessagingTemplate).convertAndSendToUser(
                eq("user2"),
                eq("/queue/messages"),
                eq(savedMessage)
        );
        verify(simpMessagingTemplate).convertAndSendToUser(
                eq("user1"),
                eq("/queue/messages"),
                eq(savedMessage)
        );
    }

    @Test
    void sendMessage_shouldSetTimestampIfNull() {
        // Arrange
        ChatMessage chatMessage = createSampleChatMessage();
        chatMessage.setTimestamp(null);
        ChatMessage savedMessage = createSavedChatMessage();

        when(chatMessageService.save(any(ChatMessage.class))).thenReturn(savedMessage);

        // Act
        ApiResponse<ChatMessage> response = chatController.sendMessage(chatMessage);

        // Assert
        assertNotNull(response);
        assertTrue(response instanceof SuccessResponse);
        assertEquals(200, response.getCode());
        assertEquals("Success", response.getMessage());
        assertEquals(savedMessage, response.getData());

        verify(chatMessageService).save(any(ChatMessage.class));
        // Verify message is sent to both receiver and sender (2 calls total)
        verify(simpMessagingTemplate, times(2)).convertAndSendToUser(anyString(), anyString(), any());
        // Verify specific calls to receiver and sender
        verify(simpMessagingTemplate).convertAndSendToUser(eq("user2"), eq("/queue/messages"), eq(savedMessage));
        verify(simpMessagingTemplate).convertAndSendToUser(eq("user1"), eq("/queue/messages"), eq(savedMessage));
    }

    @Test
    void sendMessage_shouldHandleMessageWithExistingTimestamp() {
        // Arrange
        ChatMessage chatMessage = createSampleChatMessage();
        chatMessage.setTimestamp("2023-01-01T10:00:00.000Z");
        ChatMessage savedMessage = createSavedChatMessage();

        when(chatMessageService.save(any(ChatMessage.class))).thenReturn(savedMessage);

        // Act
        ApiResponse<ChatMessage> response = chatController.sendMessage(chatMessage);

        // Assert
        assertNotNull(response);
        assertEquals(savedMessage, response.getData());
        verify(chatMessageService).save(any(ChatMessage.class));
        // Verify message is sent to both receiver and sender
        verify(simpMessagingTemplate).convertAndSendToUser(eq("user2"), eq("/queue/messages"), eq(savedMessage));
        verify(simpMessagingTemplate).convertAndSendToUser(eq("user1"), eq("/queue/messages"), eq(savedMessage));
    }

    private ChatMessage createSampleChatMessage() {
        return ChatMessage.builder()
                .senderId("user1")
                .receiverId("user2")
                .content("Hello World")
                .build();
    }

    private ChatMessage createSavedChatMessage() {
        return ChatMessage.builder()
                .id("msg1")
                .conversationId("conv1")
                .senderId("user1")
                .receiverId("user2")
                .content("Hello World")
                .timestamp(new Date().toString())
                .read(false)
                .sender(new ChatMessage.SenderInfo("user1", "John Doe"))
                .build();
    }
}
