package com.surest.member.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void testConstructor() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
        assertEquals("Not found", ex.getMessage());
    }

    @Test
    void testConstructorWithCause() {
        Throwable cause = new RuntimeException("Cause");
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found", cause);
        assertEquals("Not found", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
