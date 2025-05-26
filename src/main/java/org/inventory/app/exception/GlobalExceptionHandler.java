package org.inventory.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.model.ErrorLog;
import org.inventory.app.service.ErrorLogService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final ErrorLogService errorLogService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ErrorResponse buildErrorResponse(HttpStatus status, String error, String message, String path) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER));
        errorResponse.setStatus(status.value());
        errorResponse.setError(error);
        errorResponse.setMessage(message);
        errorResponse.setPath(path);
        return errorResponse;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("ResourceNotFoundException at [{}]: {}", request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request.getRequestURI()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExists(AlreadyExistsException ex, HttpServletRequest request) {
        log.warn("AlreadyExistsException at [{}]: {}", request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.CONFLICT, "Already Exists", ex.getMessage(), request.getRequestURI()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {
        log.warn("DuplicateResourceException at [{}]: {}", request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.CONFLICT, "Duplicate Resource", ex.getMessage(), request.getRequestURI()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        String path = request.getRequestURI();
        String message = "The operation could not be completed because it would violate data integrity rules.";

        log.warn("Data integrity violation at [{}]: {}", path, ex.getMostSpecificCause().getMessage());

        return new ResponseEntity<>(
                buildErrorResponse(
                        HttpStatus.CONFLICT,
                        "Data Integrity Violation",
                        message,
                        path
                ),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        String message = "Validation Failed: " + errors;
        log.warn("Validation error at [{}]: {}", request.getRequestURI(), message);

        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", message, request.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(HttpServletRequest request) {
        log.warn("Access denied at [{}]", request.getRequestURI());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.FORBIDDEN, "Access Denied", "You don't have permission to access this resource.", request.getRequestURI()),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(HttpServletRequest request) {
        log.warn("Bad credentials attempt at [{}]", request.getRequestURI());
        return new ResponseEntity<>(
                buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid username or password", request.getRequestURI()),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(EntityHasAssociatedItemsException.class)
    public ResponseEntity<ErrorResponse> handleEntityHasAssociatedItemsException(EntityHasAssociatedItemsException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);
        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI()
        );
        saveErrorLog(errorResponse, ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    private void saveErrorLog(ErrorResponse errorResponse, Exception ex) {
        ErrorLog log = ErrorLog.builder()
                .timestamp(errorResponse.getTimestamp())
                .status(errorResponse.getStatus())
                .error(errorResponse.getError())
                .message(errorResponse.getMessage())
                .path(errorResponse.getPath())
                .stackTrace(getStackTraceString(ex))
                .build();

        errorLogService.save(log);
    }

    private String getStackTraceString(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
