package com.surest.member.exception;

import com.surest.member.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", response.getBody().getMessage());
    }

    @Test
    void testHandleInvalidUser() {
        InvalidUserException ex = new InvalidUserException("Invalid user");
        ResponseEntity<ErrorResponse> response = handler.handleInvalidUser(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid user", response.getBody().getMessage());
    }

    @Test
    void testHandleUserAlreadyExists() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("User exists");
        ResponseEntity<ErrorResponse> response = handler.handleUserAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User exists", response.getBody().getMessage());
    }

    @Test
    void testHandleInvalidEmail() {
        InvalidEmailException ex = new InvalidEmailException("Invalid email");
        ResponseEntity<ErrorResponse> response = handler.handleInvalidEmailException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid email", response.getBody().getMessage());
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new Exception("Generic error");
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Generic error", response.getBody().getMessage());
    }
}
