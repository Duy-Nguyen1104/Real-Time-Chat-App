package com.chat_app.web_socket_chat_application.util;

import com.chat_app.web_socket_chat_application.api.response.ApiResponse;
import com.chat_app.web_socket_chat_application.api.response.ErrorResponse;
import com.chat_app.web_socket_chat_application.api.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Utility class for creating consistent API responses.
 * Centralizes response creation logic to ensure uniform response structure.
 */
public class ResponseUtil {
    
    /**
     * Create a success response with data
     * @param data The data to include in the response
     * @param <T> Type of the data
     * @return SuccessResponse with the data
     */
    public static <T> SuccessResponse<T> success(T data) {
        return new SuccessResponse<>(data);
    }
    
    /**
     * Create a success response with custom message and data
     * Note: SuccessResponse class uses "Success" as default message
     * @param message Custom success message (currently ignored due to class design)
     * @param data The data to include in the response
     * @param <T> Type of the data
     * @return SuccessResponse with the data
     */
    public static <T> SuccessResponse<T> success(String message, T data) {
        // Note: SuccessResponse doesn't have constructor with message parameter
        // Using existing constructor and the message will be "Success"
        return new SuccessResponse<>(data);
    }
    
    /**
     * Create a success response with no data
     * @return SuccessResponse with null data
     */
    public static SuccessResponse<Void> success() {
        return new SuccessResponse<>();
    }
    
    /**
     * Create a success response with custom message and no data
     * @param message Custom success message (currently ignored due to class design)
     * @return SuccessResponse with null data
     */
    public static SuccessResponse<Void> success(String message) {
        return new SuccessResponse<>();
    }
    
    /**
     * Create an error response with message and status code
     * @param message Error message
     * @param statusCode HTTP status code
     * @return ErrorResponse with the error details
     */
    public static ErrorResponse<Void> error(String message, int statusCode) {
        return new ErrorResponse<>(message, statusCode);
    }
    
    /**
     * Create an error response with message (default 500 status)
     * @param message Error message
     * @return ErrorResponse with 500 status code
     */
    public static ErrorResponse<Void> error(String message) {
        return error(message, 500);
    }
    
    /**
     * Create a bad request error response
     * @param message Error message
     * @return ErrorResponse with 400 status code
     */
    public static ErrorResponse<Void> badRequest(String message) {
        return error(message, 400);
    }
    
    /**
     * Create an unauthorized error response
     * @param message Error message
     * @return ErrorResponse with 401 status code
     */
    public static ErrorResponse<Void> unauthorized(String message) {
        return error(message, 401);
    }
    
    /**
     * Create a forbidden error response
     * @param message Error message
     * @return ErrorResponse with 403 status code
     */
    public static ErrorResponse<Void> forbidden(String message) {
        return error(message, 403);
    }
    
    /**
     * Create a not found error response
     * @param message Error message
     * @return ErrorResponse with 404 status code
     */
    public static ErrorResponse<Void> notFound(String message) {
        return error(message, 404);
    }
    
    /**
     * Create a conflict error response
     * @param message Error message
     * @return ErrorResponse with 409 status code
     */
    public static ErrorResponse<Void> conflict(String message) {
        return error(message, 409);
    }
    
    /**
     * Create an internal server error response
     * @param message Error message
     * @return ErrorResponse with 500 status code
     */
    public static ErrorResponse<Void> internalServerError(String message) {
        return error(message, 500);
    }
    
    /**
     * Create error response from validation errors map
     * @param validationErrors Map of field names to error messages
     * @return ErrorResponse with validation error message
     */
    public static ErrorResponse<Void> validationError(Map<String, String> validationErrors) {
        if (validationErrors.isEmpty()) {
            return badRequest("Validation failed");
        }
        
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        validationErrors.forEach((field, message) -> 
            errorMessage.append(field).append(" - ").append(message).append("; "));
        
        return badRequest(errorMessage.toString().trim());
    }
    
    /**
     * Create ResponseEntity with success response
     * @param data The data to include
     * @param <T> Type of the data
     * @return ResponseEntity with success response and OK status
     */
    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity.ok(success(data));
    }
    
    /**
     * Create ResponseEntity with success response and custom message
     * @param message Custom message
     * @param data The data to include
     * @param <T> Type of the data
     * @return ResponseEntity with success response and OK status
     */
    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(success(message, data));
    }
    
    /**
     * Create ResponseEntity with success response and no data
     * @return ResponseEntity with success response and OK status
     */
    public static ResponseEntity<ApiResponse<Void>> ok() {
        return ResponseEntity.ok(success());
    }
    
    /**
     * Create ResponseEntity with error response
     * @param message Error message
     * @param status HTTP status
     * @return ResponseEntity with error response
     */
    public static ResponseEntity<ApiResponse<Void>> errorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(error(message, status.value()));
    }
    
    /**
     * Create ResponseEntity with bad request error
     * @param message Error message
     * @return ResponseEntity with 400 status
     */
    public static ResponseEntity<ApiResponse<Void>> badRequestResponse(String message) {
        return errorResponse(message, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Create ResponseEntity with not found error
     * @param message Error message
     * @return ResponseEntity with 404 status
     */
    public static ResponseEntity<ApiResponse<Void>> notFoundResponse(String message) {
        return errorResponse(message, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Create ResponseEntity with internal server error
     * @param message Error message
     * @return ResponseEntity with 500 status
     */
    public static ResponseEntity<ApiResponse<Void>> internalServerErrorResponse(String message) {
        return errorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
