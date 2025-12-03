package com.surest.member.service;

import com.surest.member.entity.Role;
import com.surest.member.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements  RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> findByName(String name) {
        return Optional.ofNullable(roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found")));
    }
}
