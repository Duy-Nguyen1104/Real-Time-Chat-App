package com.chat_app.web_socket_chat_application.api.controller;

import com.chat_app.web_socket_chat_application.api.dto.UserResponseDTO;
import com.chat_app.web_socket_chat_application.api.response.ApiResponse;
import com.chat_app.web_socket_chat_application.app.exceptions.ExceptionAdviceHandle;
import com.chat_app.web_socket_chat_application.app.service.UserService;
import com.chat_app.web_socket_chat_application.domain.entity.User;
import com.chat_app.web_socket_chat_application.domain.repository.UserRepository;
import com.chat_app.web_socket_chat_application.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new ExceptionAdviceHandle())
                .build();
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() throws Exception {
        // Arrange
        List<User> users = Arrays.asList(
                createSampleUser("user1", "John Doe", "1234567890"),
                createSampleUser("user2", "Jane Smith", "0987654321")
        );

        List<UserResponseDTO> userResponseDTOs = Arrays.asList(
                createSampleUserResponseDTO("user1", "John Doe", "1234567890"),
                createSampleUserResponseDTO("user2", "Jane Smith", "0987654321")
        );

        when(userService.findAllUsers()).thenReturn(users);
        when(userMapper.toUserResponseDTOList(users)).thenReturn(userResponseDTOs);

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("John Doe"))
                .andExpect(jsonPath("$.data[1].name").value("Jane Smith"));

        verify(userService).findAllUsers();
        verify(userMapper).toUserResponseDTOList(users);
    }

    @Test
    void searchUsers_shouldReturnFilteredUsers() throws Exception {
        // Arrange
        String query = "John";
        String currentUserPhone = "1234567890";
        String currentUserId = "currentUser";

        User currentUser = createSampleUser(currentUserId, "Current User", currentUserPhone);
        List<User> filteredUsers = Arrays.asList(
                createSampleUser("user1", "John Doe", "1111111111")
        );

        List<UserResponseDTO> userResponseDTOs = Arrays.asList(
                createSampleUserResponseDTO("user1", "John Doe", "1111111111")
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentUserPhone);
        when(userRepository.findByPhoneNumber(currentUserPhone)).thenReturn(currentUser);
        when(userService.searchUsers(query, currentUserId)).thenReturn(filteredUsers);
        when(userMapper.toUserResponseDTOList(filteredUsers)).thenReturn(userResponseDTOs);

        // Act & Assert
        mockMvc.perform(get("/api/users/search?query={query}", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("John Doe"));

        verify(userService).searchUsers(query, currentUserId);
        verify(userMapper).toUserResponseDTOList(filteredUsers);
    }

    @Test
    void searchUsers_shouldReturnErrorWhenCurrentUserNotFound() throws Exception {
        // Arrange
        String query = "John";
        String currentUserPhone = "1234567890";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentUserPhone);
        when(userRepository.findByPhoneNumber(currentUserPhone)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/users/search?query={query}", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Error: Current user not found based on token."))
                .andExpect(jsonPath("$.code").value(404));

        verify(userRepository).findByPhoneNumber(currentUserPhone);
        verifyNoInteractions(userService);
    }

    @Test
    void getUserById_shouldReturnSpecificUser() throws Exception {
        // Arrange
        String userId = "user1";
        User user = createSampleUser(userId, "John Doe", "1234567890");
        UserResponseDTO userResponseDTO = createSampleUserResponseDTO(userId, "John Doe", "1234567890");

        when(userService.getUserById(userId)).thenReturn(user);
        when(userMapper.toUserResponseDTO(user)).thenReturn(userResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.phoneNumber").value("1234567890"));

        verify(userService).getUserById(userId);
        verify(userMapper).toUserResponseDTO(user);
    }

    @Test
    void getAllUsers_directCallToController_shouldReturnCorrectResponse() {
        // Arrange
        List<User> users = Arrays.asList(
                createSampleUser("user1", "John Doe", "1234567890")
        );

        List<UserResponseDTO> userResponseDTOs = Arrays.asList(
                createSampleUserResponseDTO("user1", "John Doe", "1234567890")
        );

        when(userService.findAllUsers()).thenReturn(users);
        when(userMapper.toUserResponseDTOList(users)).thenReturn(userResponseDTOs);

        // Act
        ApiResponse<List<UserResponseDTO>> response = userController.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals("Success", response.getMessage());
        assertEquals(200, response.getCode());
        assertEquals(userResponseDTOs, response.getData());
        verify(userService).findAllUsers();
        verify(userMapper).toUserResponseDTOList(users);
    }

    @Test
    void searchUsers_directCallToController_shouldReturnCorrectResponse() {
        // Arrange
        String query = "John";
        String currentUserPhone = "1234567890";
        String currentUserId = "currentUser";

        User currentUser = createSampleUser(currentUserId, "Current User", currentUserPhone);
        List<User> filteredUsers = Arrays.asList(
                createSampleUser("user1", "John Doe", "1111111111")
        );

        List<UserResponseDTO> userResponseDTOs = Arrays.asList(
                createSampleUserResponseDTO("user1", "John Doe", "1111111111")
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentUserPhone);
        when(userRepository.findByPhoneNumber(currentUserPhone)).thenReturn(currentUser);
        when(userService.searchUsers(query, currentUserId)).thenReturn(filteredUsers);
        when(userMapper.toUserResponseDTOList(filteredUsers)).thenReturn(userResponseDTOs);

        // Act
        ApiResponse<List<UserResponseDTO>> response = userController.searchUsers(query);

        // Assert
        assertNotNull(response);
        assertEquals("Success", response.getMessage());
        assertEquals(200, response.getCode());
        assertEquals(userResponseDTOs, response.getData());
        verify(userService).searchUsers(query, currentUserId);
        verify(userMapper).toUserResponseDTOList(filteredUsers);
    }

    @Test
    void getUserById_directCallToController_shouldReturnCorrectResponse() {
        // Arrange
        String userId = "user1";
        User user = createSampleUser(userId, "John Doe", "1234567890");
        UserResponseDTO userResponseDTO = createSampleUserResponseDTO(userId, "John Doe", "1234567890");

        when(userService.getUserById(userId)).thenReturn(user);
        when(userMapper.toUserResponseDTO(user)).thenReturn(userResponseDTO);

        // Act
        ApiResponse<UserResponseDTO> response = userController.getUserById(userId);

        // Assert
        assertNotNull(response);
        assertEquals("Success", response.getMessage());
        assertEquals(200, response.getCode());
        assertEquals(userResponseDTO, response.getData());
        verify(userService).getUserById(userId);
        verify(userMapper).toUserResponseDTO(user);
    }

    @Test
    void searchUsers_directCallToController_shouldReturnErrorWhenCurrentUserNotFound() {
        // Arrange
        String query = "John";
        String currentUserPhone = "1234567890";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentUserPhone);
        when(userRepository.findByPhoneNumber(currentUserPhone)).thenReturn(null);

        // Act
        ApiResponse<List<UserResponseDTO>> response = userController.searchUsers(query);

        // Assert
        assertNotNull(response);
        assertEquals("Error: Current user not found based on token.", response.getMessage());
        assertEquals(404, response.getCode());
        verify(userRepository).findByPhoneNumber(currentUserPhone);
        verifyNoInteractions(userService);
    }

    @Test
    void getUserById_shouldHandleServiceException() throws Exception {
        // Arrange
        String userId = "user1";

        when(userService.getUserById(userId)).thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().is5xxServerError());

        verify(userService).getUserById(userId);
    }

    private User createSampleUser(String id, String name, String phoneNumber) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setPhoneNumber(phoneNumber);
        user.setStatus("online");
        return user;
    }

    private UserResponseDTO createSampleUserResponseDTO(String id, String name, String phoneNumber) {
        return UserResponseDTO.builder()
                .id(id)
                .name(name)
                .phoneNumber(phoneNumber)
                .status("online")
                .build();
    }
}
