package com.platform.marketing.service;

import com.platform.marketing.entity.Permission;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PermissionService {
    List<Permission> findAll();
    Optional<Permission> findById(String id);
    Permission create(Permission permission);
    Permission update(String id, Permission permission);
    void delete(String id);
    void deleteBatch(List<String> ids);

    void updateStatus(String id, boolean status);

    Page<Permission> search(String keyword, String type, Boolean status, Pageable pageable);

    List<com.platform.marketing.dto.PermissionTreeNode> getTree();
}
