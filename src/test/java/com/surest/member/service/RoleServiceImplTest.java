package com.surest.member.service;

import com.surest.member.entity.Role;
import com.surest.member.repository.RoleRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceImplTest {

    @Test
    void findByName_success() {
        RoleRepository repo = mock(RoleRepository.class);
        RoleServiceImpl service = new RoleServiceImpl(repo);

        Role role = new Role();
        role.setName("ADMIN");

        when(repo.findByName("ADMIN")).thenReturn(Optional.of(role));

        Optional<Role> result = service.findByName("ADMIN");

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getName());
    }

    @Test
    void findByName_notFound() {
        RoleRepository repo = mock(RoleRepository.class);
        RoleServiceImpl service = new RoleServiceImpl(repo);

        when(repo.findByName("ADMIN")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findByName("ADMIN"));
    }
}
