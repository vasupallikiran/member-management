package com.surest.member.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserAlreadyExistsExceptionTest {

    @Test
    void testConstructor() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("User exists");
        assertEquals("User exists", ex.getMessage());
    }

    @Test
    void testConstructorWithCause() {
        Throwable cause = new RuntimeException("Cause");
        UserAlreadyExistsException ex = new UserAlreadyExistsException("User exists", cause);
        assertEquals("User exists", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
