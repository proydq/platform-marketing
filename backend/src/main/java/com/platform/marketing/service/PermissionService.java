package com.platform.marketing.service;

import com.platform.marketing.entity.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionService {
    List<Permission> findAll();
    Optional<Permission> findById(String id);
    Permission create(Permission permission);
    Permission update(String id, Permission permission);
    void delete(String id);
}
