package com.surest.member.service;

import com.surest.member.entity.Role;

import java.util.Optional;

public interface RoleService {
    Optional<Role>  findByName(String name);
}
