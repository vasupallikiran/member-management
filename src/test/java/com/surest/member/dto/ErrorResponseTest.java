package com.surest.member.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void testConstructorAndGetters() {
        ErrorResponse er = new ErrorResponse(400, "Bad", 123L);

        assertEquals(400, er.getStatus());
        assertEquals("Bad", er.getMessage());
        assertEquals(123L, er.getTimestamp());
    }
}
