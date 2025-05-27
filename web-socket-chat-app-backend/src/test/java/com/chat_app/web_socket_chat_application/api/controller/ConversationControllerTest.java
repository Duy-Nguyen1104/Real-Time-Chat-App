package com.chat_app.web_socket_chat_application.api.controller;

import com.chat_app.web_socket_chat_application.api.dto.ConversationDTO;
import com.chat_app.web_socket_chat_application.api.dto.CreateConversationDTO;
import com.chat_app.web_socket_chat_application.api.response.ApiResponse;
import com.chat_app.web_socket_chat_application.app.exceptions.ExceptionAdviceHandle;
import com.chat_app.web_socket_chat_application.app.service.ConversationService;
import com.chat_app.web_socket_chat_application.domain.entity.Conversation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ConversationControllerTest {

    @Mock
    private ConversationService conversationService;

    @InjectMocks
    private ConversationController conversationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(conversationController)
        .setControllerAdvice(new ExceptionAdviceHandle())
        .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllConversations_shouldReturnAllConversations() throws Exception {
        // Arrange
        List<Conversation> conversations = Arrays.asList(
                createSampleConversation("conv1", "user1", "user2"),
                createSampleConversation("conv2", "user1", "user3")
        );

        when(conversationService.getAllConversations()).thenReturn(conversations);

        // Act & Assert
        mockMvc.perform(get("/conversations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value("conv1"))
                .andExpect(jsonPath("$.data[1].id").value("conv2"));

        verify(conversationService).getAllConversations();
    }

    @Test
    void getUserConversations_shouldReturnUserConversations() throws Exception {
        // Arrange
        String userId = "user1";
        List<ConversationDTO> conversationDTOs = Arrays.asList(
                createSampleConversationDTO("conv1", "John Doe"),
                createSampleConversationDTO("conv2", "Jane Smith")
        );

        when(conversationService.getUserConversations(userId)).thenReturn(conversationDTOs);

        // Act & Assert
        mockMvc.perform(get("/conversations/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].displayName").value("John Doe"))
                .andExpect(jsonPath("$.data[1].displayName").value("Jane Smith"));

        verify(conversationService).getUserConversations(userId);
    }

    @Test
    void getConversation_shouldReturnSpecificConversation() throws Exception {
        // Arrange
        String conversationId = "conv1";
        Conversation conversation = createSampleConversation(conversationId, "user1", "user2");

        when(conversationService.getConversation(conversationId)).thenReturn(conversation);

        // Act & Assert
        mockMvc.perform(get("/conversations/{id}", conversationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(conversationId))
                .andExpect(jsonPath("$.data.senderId").value("user1"))
                .andExpect(jsonPath("$.data.receiverId").value("user2"));

        verify(conversationService).getConversation(conversationId);
    }

    @Test
    void createOrGetConversation_shouldCreateNewConversation() throws Exception {
        // Arrange
        CreateConversationDTO createDTO = new CreateConversationDTO();
        createDTO.setSenderId("user1");
        createDTO.setReceiverId("user2");

        Conversation conversation = createSampleConversation("conv1", "user1", "user2");

        when(conversationService.createOrGetConversation("user1", "user2")).thenReturn(conversation);

        // Act & Assert
        mockMvc.perform(post("/conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value("conv1"))
                .andExpect(jsonPath("$.data.senderId").value("user1"))
                .andExpect(jsonPath("$.data.receiverId").value("user2"));

        verify(conversationService).createOrGetConversation("user1", "user2");
    }

    @Test
    void markAsRead_shouldMarkConversationAsRead() throws Exception {
        // Arrange
        String conversationId = "conv1";
        doNothing().when(conversationService).markAsRead(conversationId);

        // Act & Assert
        mockMvc.perform(post("/conversations/{id}/read", conversationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.code").value(200));

        verify(conversationService).markAsRead(conversationId);
    }

    @Test
    void getAllConversations_directCallToController_shouldReturnCorrectResponse() {
        // Arrange
        List<Conversation> conversations = Arrays.asList(
                createSampleConversation("conv1", "user1", "user2")
        );

        when(conversationService.getAllConversations()).thenReturn(conversations);

        // Act
        ApiResponse<List<Conversation>> response = conversationController.getAllConversations();

        // Assert
        assertNotNull(response);
        assertEquals("Success", response.getMessage());
        assertEquals(200, response.getCode());
        assertEquals(conversations, response.getData());
        verify(conversationService).getAllConversations();
    }

    @Test
    void getUserConversations_directCallToController_shouldReturnCorrectResponse() {
        // Arrange
        String userId = "user1";
        List<ConversationDTO> conversationDTOs = Arrays.asList(
                createSampleConversationDTO("conv1", "John Doe")
        );

        when(conversationService.getUserConversations(userId)).thenReturn(conversationDTOs);

        // Act
        ApiResponse<List<ConversationDTO>> response = conversationController.getUserConversations(userId);

        // Assert
        assertNotNull(response);
        assertEquals("Success", response.getMessage());
        assertEquals(200, response.getCode());
        assertEquals(conversationDTOs, response.getData());
        verify(conversationService).getUserConversations(userId);
    }

    @Test
    void getConversation_directCallToController_shouldReturnCorrectResponse() {
        // Arrange
        String conversationId = "conv1";
        Conversation conversation = createSampleConversation(conversationId, "user1", "user2");

        when(conversationService.getConversation(conversationId)).thenReturn(conversation);

        // Act
        ApiResponse<Conversation> response = conversationController.getConversation(conversationId);

        // Assert
        assertNotNull(response);
        assertEquals(conversation, response.getData());
        verify(conversationService).getConversation(conversationId);
    }

    @Test
    void createOrGetConversation_directCallToController_shouldReturnCorrectResponse() {
        // Arrange
        CreateConversationDTO createDTO = new CreateConversationDTO();
        createDTO.setSenderId("user1");
        createDTO.setReceiverId("user2");

        Conversation conversation = createSampleConversation("conv1", "user1", "user2");

        when(conversationService.createOrGetConversation("user1", "user2")).thenReturn(conversation);

        // Act
        ApiResponse<Conversation> response = conversationController.createOrGetConversation(createDTO);

        // Assert
        assertNotNull(response);
        assertEquals("Success", response.getMessage());
        assertEquals(200, response.getCode());
        assertEquals(conversation, response.getData());
        verify(conversationService).createOrGetConversation("user1", "user2");
    }

    @Test
    void markAsRead_directCallToController_shouldReturnSuccessResponse() {
        // Arrange
        String conversationId = "conv1";
        doNothing().when(conversationService).markAsRead(conversationId);

        // Act
        ApiResponse<?> response = conversationController.markAsRead(conversationId);

        // Assert
        assertNotNull(response);
        assertEquals("Success", response.getMessage());
        assertEquals(200, response.getCode());
        verify(conversationService).markAsRead(conversationId);
    }

    @Test
    void createOrGetConversation_shouldHandleServiceException() throws Exception {
        // Arrange
        CreateConversationDTO createDTO = new CreateConversationDTO();
        createDTO.setSenderId("user1");
        createDTO.setReceiverId("user2");

        when(conversationService.createOrGetConversation("user1", "user2"))
                .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(post("/conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().is5xxServerError());

        verify(conversationService).createOrGetConversation("user1", "user2");
    }

    private Conversation createSampleConversation(String id, String senderId, String receiverId) {
        Conversation conversation = new Conversation();
        conversation.setId(id);
        conversation.setSenderId(senderId);
        conversation.setReceiverId(receiverId);
        conversation.setLastMessage("Hello");
        conversation.setLastMessageTime("2023-01-01T10:00:00Z");
        conversation.setUnreadCount(0);
        conversation.setOnline(false);
        conversation.setCategory("all");
        conversation.setAvatarColor("#FF5733");
        return conversation;
    }

    private ConversationDTO createSampleConversationDTO(String id, String displayName) {
        return ConversationDTO.builder()
                .id(id)
                .displayName(displayName)
                .lastMessage("Hello")
                .lastMessageTime("2023-01-01T10:00:00Z")
                .unreadCount(0)
                .online(false)
                .category("all")
                .avatarColor("#FF5733")
                .senderId("user1")
                .receiverId("user2")
                .chatId("user1_user2")
                .build();
    }
}
