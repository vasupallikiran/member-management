package com.surest.member.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MemberDTOTest {

    @Test
    void testGetterSetter() {
        MemberDTO dto = new MemberDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("a@b.com");
        dto.setDateOfBirth(LocalDate.now());

        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("a@b.com", dto.getEmail());
        assertNotNull(dto.getDateOfBirth());
    }

    @Test
    void testAllArgsConstructor() {
        MemberDTO dto = new MemberDTO(
                null, "John", "Doe", LocalDate.now(), "a@b.com", null, null
        );

        assertEquals("John", dto.getFirstName());
    }
}
