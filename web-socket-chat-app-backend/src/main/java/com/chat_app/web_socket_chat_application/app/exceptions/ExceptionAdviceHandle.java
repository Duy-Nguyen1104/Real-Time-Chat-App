package com.chat_app.web_socket_chat_application.app.exceptions;

import com.chat_app.web_socket_chat_application.api.response.ApiResponse;
import com.chat_app.web_socket_chat_application.api.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@Slf4j
public class ExceptionAdviceHandle {
    @ExceptionHandler(value = AppException.class)
    public ApiResponse<?> handleAppException(AppException e) {
        log.error(String.format("%s : %s ", e.getClass().getSimpleName(), e.getMessage()), e);
        return new ErrorResponse<>( e.getMessage(), e.getCode(), null);
    }

    @ExceptionHandler(value = DataAccessResourceFailureException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiResponse<?> handleDataAccessResourceFailureException(DataAccessResourceFailureException e) {
        log.error(String.format("%s : %s ", e.getClass().getSimpleName(), e.getMessage()), e);
        return new ErrorResponse<>("Database connection temporarily unavailable. Please try again later.", 
                                   HttpStatus.SERVICE_UNAVAILABLE.value());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> internalServerError(Exception e) {
        log.error(String.format("%s : %s ", e.getClass().getSimpleName(), e.getMessage()), e);
        return new ErrorResponse<>("The request is temporarily interrupted. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResponse<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(String.format("%s : %s ", e.getClass().getSimpleName(), e.getMessage()), e);
        
        // Extract field errors from the validation exception
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        return new ErrorResponse<>("Invalid Input", HttpStatus.BAD_REQUEST.value(), errors);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ApiResponse<?> handleConstraintViolationException(ConstraintViolationException e) {
        log.error(String.format("%s : %s ", e.getClass().getSimpleName(), e.getMessage()), e);

        List<FieldError> errors = new ArrayList<>();
        e.getConstraintViolations().forEach(cv ->
                errors.add(new FieldError(
                        cv.getPropertyPath().toString(),
                        cv.getPropertyPath().toString().split("\\.")[1],
                        cv.getInvalidValue(),
                        false,
                        new String[]{cv.getPropertyPath().toString()},
                        null,
                        cv.getMessage()
                )));
        return new ErrorResponse<>("Invalid Input", errors);
    }

}
