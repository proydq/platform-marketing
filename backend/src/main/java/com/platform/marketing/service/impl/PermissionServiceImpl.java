package com.platform.marketing.service.impl;

import com.platform.marketing.entity.Permission;
import com.platform.marketing.repository.PermissionRepository;
import com.platform.marketing.service.PermissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @Override
    public Page<Permission> search(String keyword, String type, Boolean status, Pageable pageable) {
        if (keyword == null) keyword = "";
        if (type == null) type = "";
        return permissionRepository.search(keyword, type, status, pageable);
    }

    @Override
    public Optional<Permission> findById(String id) {
        return permissionRepository.findById(id);
    }

    @Override
    @Transactional
    public Permission create(Permission permission) {
        if (permissionRepository.existsByCode(permission.getCode())) {
            throw new IllegalArgumentException("Permission code already exists");
        }
        permission.setCreatedBy(currentUser());
        permission.setUpdatedBy(permission.getCreatedBy());
        return permissionRepository.save(permission);
    }

    @Override
    @Transactional
    public Permission update(String id, Permission permission) {
        Permission existing = permissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found"));
        if (!existing.getCode().equals(permission.getCode()) &&
                permissionRepository.existsByCode(permission.getCode())) {
            throw new IllegalArgumentException("Permission code already exists");
        }
        existing.setName(permission.getName());
        existing.setCode(permission.getCode());
        existing.setDescription(permission.getDescription());
        existing.setType(permission.getType());
        existing.setParentId(permission.getParentId());
        existing.setUrl(permission.getUrl());
        existing.setMethod(permission.getMethod());
        existing.setGroup(permission.getGroup());
        existing.setStatus(permission.isStatus());
        existing.setUpdatedBy(currentUser());
        return permissionRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        permissionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteBatch(List<String> ids) {
        ids.forEach(permissionRepository::deleteById);
    }

    @Override
    public List<com.platform.marketing.dto.PermissionTreeNode> getTree() {
        List<Permission> all = permissionRepository.findAll();
        java.util.Map<String, com.platform.marketing.dto.PermissionTreeNode> map = new java.util.HashMap<>();
        for (Permission p : all) {
            com.platform.marketing.dto.PermissionTreeNode node = new com.platform.marketing.dto.PermissionTreeNode();
            node.setId(p.getId());
            node.setName(p.getName());
            node.setCode(p.getCode());
            node.setParentId(p.getParentId());
            node.setType(p.getType());
            node.setUrl(p.getUrl());
            node.setMethod(p.getMethod());
            node.setStatus(p.isStatus());
            node.setDescription(p.getDescription());
            map.put(p.getId(), node);
        }
        List<com.platform.marketing.dto.PermissionTreeNode> roots = new java.util.ArrayList<>();
        for (com.platform.marketing.dto.PermissionTreeNode n : map.values()) {
            if (n.getParentId() == null || n.getParentId().isEmpty()) {
                roots.add(n);
            } else {
                com.platform.marketing.dto.PermissionTreeNode parent = map.get(n.getParentId());
                if (parent != null) {
                    parent.getChildren().add(n);
                } else {
                    roots.add(n);
                }
            }
        }
        return roots;
    }

    private String currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonymous";
    }
}
