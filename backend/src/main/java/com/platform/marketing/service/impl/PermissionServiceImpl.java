package com.platform.marketing.service.impl;

import com.platform.marketing.entity.Permission;
import com.platform.marketing.repository.PermissionRepository;
import com.platform.marketing.service.PermissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Permission> search(String keyword, Pageable pageable) {
        if (keyword == null) keyword = "";
        return permissionRepository
                .findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(keyword, keyword, pageable);
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
}
