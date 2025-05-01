package org.inventory.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(AlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        errorResponse.setTimestamp(timestamp);
        errorResponse.setStatus(HttpStatus.CONFLICT.value());
        errorResponse.setError("Already Exists!!");
        errorResponse.setMessage(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        ErrorResponse errorResponse = new ErrorResponse();
        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        errorResponse.setTimestamp(timestamp);
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setError("Argument Not Valid!!");
        String message = "Validation Failed: " + errors;
        errorResponse.setMessage(message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        errorResponse.setTimestamp(timestamp);
        errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
        errorResponse.setError("Access Denied.!!");
        String message = "Access Denied. You don't have permission to access this resource.";
        errorResponse.setMessage(message);
        errorResponse.setPath(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}
