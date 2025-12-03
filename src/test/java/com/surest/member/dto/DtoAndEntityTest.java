package com.surest.member.dto;

import com.surest.member.entity.Member;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DtoAndEntityTest {

    @Test
    void testErrorResponse() {
        ErrorResponse response = new ErrorResponse(400, "Bad Request", System.currentTimeMillis());

        assertEquals(400, response.getStatus());
        assertEquals("Bad Request", response.getMessage());
        assertTrue(response.getTimestamp() > 0);

        response.setMessage("Updated");
        assertEquals("Updated", response.getMessage());
    }

    @Test
    void testLoginRequest() {
        LoginRequest req = new LoginRequest("user", "pass");

        assertEquals("user", req.getUsername());
        assertEquals("pass", req.getPassword());
    }

    @Test
    void testLoginResponse() {
        LoginResponse res = new LoginResponse("token123");

        assertEquals("token123", res.getToken());

        res.setToken("newToken");
        assertEquals("newToken", res.getToken());
    }

    @Test
    void testRegisterRequest() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("admin");
        req.setPassword("123");
        req.setRole("ADMIN");

        assertEquals("admin", req.getUsername());
        assertEquals("123", req.getPassword());
        assertEquals("ADMIN", req.getRole());
    }

    @Test
    void testMemberDTO() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        MemberDTO dto = new MemberDTO(
                id, "John", "Doe",
                LocalDate.of(1990, 1, 1),
                "email@test.com",
                now,
                now
        );

        assertEquals(id, dto.getId());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("email@test.com", dto.getEmail());

        dto.setFirstName("Jane");
        assertEquals("Jane", dto.getFirstName());
    }

    @Test
    void testMemberEntityLifecycle() {
        Member member = new Member();
        member.setFirstName("John");
        member.setLastName("Doe");
        member.setDateOfBirth(LocalDate.of(1980, 1, 1));
        member.setEmail("test@test.com");

        // PrePersist - simulate JPA lifecycle
        member.onCreate();
        assertNotNull(member.getCreatedAt());
        assertNotNull(member.getUpdatedAt());

        LocalDateTime previousUpdate = member.getUpdatedAt();

        // PreUpdate - simulate JPA lifecycle
        member.onUpdate();
        assertTrue(member.getUpdatedAt().isAfter(previousUpdate));
    }
}

