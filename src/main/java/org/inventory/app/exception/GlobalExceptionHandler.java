package org.inventory.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        errorResponse.setTimestamp(timestamp);
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setError("Not Found");
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        errorResponse.setTimestamp(timestamp);
        errorResponse.setStatus(HttpStatus.CONFLICT.value());
        errorResponse.setError("User Already Exists!!");
        errorResponse.setMessage(ex.getMessage() + " Please try again with a different username.");
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
