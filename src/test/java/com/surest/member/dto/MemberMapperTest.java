package com.surest.member.dto;

import com.surest.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MemberMapperTest {

    private final MemberMapper mapper = Mappers.getMapper(MemberMapper.class);

    @Test
    void testToDTO() {
        Member member = new Member();
        member.setId(UUID.randomUUID());
        member.setFirstName("John");
        member.setLastName("Doe");
        member.setEmail("test@test.com");
        member.setDateOfBirth(LocalDate.of(1990, 1, 1));
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());

        MemberDTO dto = mapper.toDTO(member);

        assertEquals(member.getId(), dto.getId());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("test@test.com", dto.getEmail());
    }

    @Test
    void testToEntity() {
        MemberDTO dto = new MemberDTO(
                UUID.randomUUID(),
                "Alice",
                "Smith",
                LocalDate.of(2000, 1, 1),
                "alice@test.com",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Member member = mapper.toEntity(dto);

        assertEquals(dto.getId(), member.getId());
        assertEquals("Alice", member.getFirstName());
        assertEquals("Smith", member.getLastName());
        assertEquals("alice@test.com", member.getEmail());
    }
}

