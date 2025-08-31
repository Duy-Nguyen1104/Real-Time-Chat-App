package com.chat_app.web_socket_chat_application.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility class for common validation operations.
 * Centralizes validation logic to reduce redundancy across controllers and services.
 */
public class ValidationUtil {
    
    // Phone number pattern - supports various formats
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,15}$");
    
    // Password requirements
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 100;
    
    // Name requirements
    public static final int MIN_NAME_LENGTH = 1;
    public static final int MAX_NAME_LENGTH = 50;
    
    /**
     * Validate password strength and requirements
     * @param password The password to validate
     * @return Map of field name to error message, empty if valid
     */
    public static Map<String, String> validatePassword(String password) {
        Map<String, String> errors = new HashMap<>();
        
        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "Password is required");
            return errors;
        }
        
        if (password.length() < MIN_PASSWORD_LENGTH) {
            errors.put("password", "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            errors.put("password", "Password must not exceed " + MAX_PASSWORD_LENGTH + " characters");
        }
        
        return errors;
    }
    
    /**
     * Validate phone number format
     * @param phoneNumber The phone number to validate
     * @return Map of field name to error message, empty if valid
     */
    public static Map<String, String> validatePhoneNumber(String phoneNumber) {
        Map<String, String> errors = new HashMap<>();
        
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            errors.put("phoneNumber", "Phone number is required");
            return errors;
        }
        
        // Remove spaces and special characters for validation
        String cleanPhone = phoneNumber.replaceAll("[\\s()-]", "");
        
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            errors.put("phoneNumber", "Invalid phone number format");
        }
        
        return errors;
    }
    
    /**
     * Validate user name
     * @param name The name to validate
     * @return Map of field name to error message, empty if valid
     */
    public static Map<String, String> validateName(String name) {
        Map<String, String> errors = new HashMap<>();
        
        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Name is required");
            return errors;
        }
        
        String trimmedName = name.trim();
        
        if (trimmedName.length() < MIN_NAME_LENGTH) {
            errors.put("name", "Name must not be empty");
        }
        
        if (trimmedName.length() > MAX_NAME_LENGTH) {
            errors.put("name", "Name must not exceed " + MAX_NAME_LENGTH + " characters");
        }
        
        return errors;
    }
    
    /**
     * Validate if two passwords match
     * @param password The original password
     * @param confirmPassword The confirmation password
     * @return Map of field name to error message, empty if valid
     */
    public static Map<String, String> validatePasswordMatch(String password, String confirmPassword) {
        Map<String, String> errors = new HashMap<>();
        
        if (password == null || confirmPassword == null) {
            errors.put("confirmPassword", "Both passwords are required");
            return errors;
        }
        
        if (!password.equals(confirmPassword)) {
            errors.put("confirmPassword", "Passwords do not match");
        }
        
        return errors;
    }
    
    /**
     * Validate complete user registration data
     * @param name User's name
     * @param phoneNumber User's phone number
     * @param password User's password
     * @param confirmPassword Password confirmation
     * @return Map of field names to error messages, empty if all valid
     */
    public static Map<String, String> validateUserRegistration(String name, String phoneNumber, 
                                                               String password, String confirmPassword) {
        Map<String, String> allErrors = new HashMap<>();
        
        allErrors.putAll(validateName(name));
        allErrors.putAll(validatePhoneNumber(phoneNumber));
        allErrors.putAll(validatePassword(password));
        allErrors.putAll(validatePasswordMatch(password, confirmPassword));
        
        return allErrors;
    }
    
    /**
     * Validate user login data
     * @param phoneNumber User's phone number
     * @param password User's password
     * @return Map of field names to error messages, empty if all valid
     */
    public static Map<String, String> validateUserLogin(String phoneNumber, String password) {
        Map<String, String> allErrors = new HashMap<>();
        
        allErrors.putAll(validatePhoneNumber(phoneNumber));
        allErrors.putAll(validatePassword(password));
        
        return allErrors;
    }
    
    /**
     * Check if string is null or empty
     * @param value The string to check
     * @return true if null or empty, false otherwise
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    /**
     * Sanitize input string by trimming whitespace
     * @param value The string to sanitize
     * @return Sanitized string, or null if input was null
     */
    public static String sanitize(String value) {
        return value == null ? null : value.trim();
    }
}
