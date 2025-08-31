package com.chat_app.web_socket_chat_application.test.base;

import com.chat_app.web_socket_chat_application.app.exceptions.ExceptionAdviceHandle;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Base test class that provides common MockMvc setup for controller tests.
 * Reduces redundancy in test setup across multiple controller test classes.
 */
public abstract class BaseControllerTest {
    
    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper;
    
    @BeforeEach
    void baseSetUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(getController())
                .setControllerAdvice(new ExceptionAdviceHandle())
                .build();
        objectMapper = new ObjectMapper();
        
        // Call custom setup method for each test class
        customSetUp();
    }
    
    /**
     * Subclasses should implement this method to return the controller instance being tested
     * @return The controller instance
     */
    protected abstract Object getController();
    
    /**
     * Optional method for subclasses to override if they need additional setup
     * This is called after the base setup is complete
     */
    protected void customSetUp() {
        // Default implementation does nothing
        // Subclasses can override this for additional setup
    }
    
    /**
     * Helper method to convert object to JSON string for request bodies
     * @param obj The object to convert
     * @return JSON string representation
     * @throws Exception if conversion fails
     */
    protected String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
    
    /**
     * Helper method to create sample test data
     * This can be overridden by subclasses for their specific test data needs
     */
    protected void createTestData() {
        // Default implementation does nothing
        // Subclasses can override this to create their test data
    }
}
