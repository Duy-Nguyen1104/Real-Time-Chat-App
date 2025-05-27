package com.chat_app.web_socket_chat_application.api.controller;

import com.chat_app.web_socket_chat_application.api.dto.AuthenticationDTO;
import com.chat_app.web_socket_chat_application.api.dto.ResetPasswordDTO;
import com.chat_app.web_socket_chat_application.api.dto.UserDTO;
import com.chat_app.web_socket_chat_application.api.response.ApiResponse;
import com.chat_app.web_socket_chat_application.api.response.AuthenticationResponse;
import com.chat_app.web_socket_chat_application.app.exceptions.ExceptionAdviceHandle;
import com.chat_app.web_socket_chat_application.app.service.AuthenticationService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController)
        .setControllerAdvice(new ExceptionAdviceHandle())
        .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void login_shouldReturnAuthenticationResponse() throws Exception {
        // Arrange
        AuthenticationDTO authDto = new AuthenticationDTO();
        authDto.setPhoneNumber("1234567890");
        authDto.setPassword("password123");

        AuthenticationResponse authResponse = new AuthenticationResponse(
                "jwt-token", "user1", "John Doe", "online"
        );

        when(authenticationService.login(any(AuthenticationDTO.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("jwt-token"))
                .andExpect(jsonPath("$.data.id").value("user1"))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.status").value("online"));

        verify(authenticationService).login(any(AuthenticationDTO.class));
    }

    @Test
    void login_shouldHandleInvalidCredentials() throws Exception {
        // Arrange
        AuthenticationDTO authDto = new AuthenticationDTO();
        authDto.setPhoneNumber("1234567890");
        authDto.setPassword("wrongpassword");

        when(authenticationService.login(any(AuthenticationDTO.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDto)))
                .andExpect(status().is5xxServerError());

        verify(authenticationService).login(any(AuthenticationDTO.class));
    }

    @Test
    void resetPassword_shouldReturnAuthenticationResponse() throws Exception {
        // Arrange
        ResetPasswordDTO resetDto = new ResetPasswordDTO();
        resetDto.setPhoneNumber("1234567890");
        resetDto.setNewPassword("newpassword123");

        AuthenticationResponse authResponse = new AuthenticationResponse(
                "new-jwt-token", "user1", "John Doe", "online"
        );

        when(authenticationService.resetPassword(any(ResetPasswordDTO.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("new-jwt-token"))
                .andExpect(jsonPath("$.data.id").value("user1"));

        verify(authenticationService).resetPassword(any(ResetPasswordDTO.class));
    }

    @Test
    void register_shouldReturnSuccessResponse() throws Exception {
        // Arrange
        UserDTO userDto = UserDTO.builder()
                .name("John Doe")
                .phoneNumber("1234567890")
                .password("password123")
                .build();

        doNothing().when(authenticationService).register(any(UserDTO.class));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.code").value(200));

        verify(authenticationService).register(any(UserDTO.class));
    }

    @Test
    void register_shouldHandleUserAlreadyExists() throws Exception {
        // Arrange
        UserDTO userDto = UserDTO.builder()
                .name("John Doe")
                .phoneNumber("1234567890")
                .password("password123")
                .build();

        doThrow(new RuntimeException("User already exists"))
                .when(authenticationService).register(any(UserDTO.class));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().is5xxServerError());

        verify(authenticationService).register(any(UserDTO.class));
    }

    @Test
    void login_directCallToController_shouldReturnCorrectResponse() {
        // Arrange
        AuthenticationDTO authDto = new AuthenticationDTO();
        authDto.setPhoneNumber("1234567890");
        authDto.setPassword("password123");

        AuthenticationResponse authResponse = new AuthenticationResponse(
                "jwt-token", "user1", "John Doe", "online"
        );

        when(authenticationService.login(any(AuthenticationDTO.class))).thenReturn(authResponse);

        // Act
        ApiResponse<AuthenticationResponse> response = authenticationController.login(authDto);

        // Assert
        assertNotNull(response);
        assertEquals("Success", response.getMessage());
        assertEquals(200, response.getCode());
        assertEquals(authResponse, response.getData());
        verify(authenticationService).login(any(AuthenticationDTO.class));
    }

    @Test
    void resetPassword_directCallToController_shouldReturnCorrectResponse() {
        // Arrange
        ResetPasswordDTO resetDto = new ResetPasswordDTO();
        resetDto.setPhoneNumber("1234567890");
        resetDto.setNewPassword("newpassword123");

        AuthenticationResponse authResponse = new AuthenticationResponse(
                "new-jwt-token", "user1", "John Doe", "online"
        );

        when(authenticationService.resetPassword(any(ResetPasswordDTO.class))).thenReturn(authResponse);

        // Act
        ApiResponse<?> response = authenticationController.directResetPassword(resetDto);

        // Assert
        assertNotNull(response);
        assertEquals("Success", response.getMessage());
        assertEquals(200, response.getCode());
        assertEquals(authResponse, response.getData());
        verify(authenticationService).resetPassword(any(ResetPasswordDTO.class));
    }

    @Test
    void register_directCallToController_shouldReturnSuccessResponse() {
        // Arrange
        UserDTO userDto = UserDTO.builder()
                .name("John Doe")
                .phoneNumber("1234567890")
                .password("password123")
                .build();

        doNothing().when(authenticationService).register(any(UserDTO.class));

        // Act
        ApiResponse<?> response = authenticationController.register(userDto);

        // Assert
        assertNotNull(response);
        assertEquals("Success", response.getMessage());
        assertEquals(200, response.getCode());
        verify(authenticationService).register(any(UserDTO.class));
    }
}