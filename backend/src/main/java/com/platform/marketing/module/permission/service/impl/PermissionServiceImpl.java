package com.platform.marketing.module.permission.service.impl;

import com.platform.marketing.module.permission.dto.PermissionQueryDTO;
import com.platform.marketing.module.permission.dto.PermissionSaveDTO;
import com.platform.marketing.module.permission.dto.PermissionVO;
import com.platform.marketing.module.permission.entity.Permission;
import com.platform.marketing.module.permission.repository.PermissionRepository;
import com.platform.marketing.module.permission.service.PermissionService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of permission service.
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Page<PermissionVO> fetchPermissions(PermissionQueryDTO queryDTO) {
        PageRequest page = PageRequest.of(queryDTO.getPage(), queryDTO.getSize());
        Page<Permission> perms = permissionRepository.search(queryDTO.getKeyword(), queryDTO.getType(), queryDTO.getStatus(), page);
        List<PermissionVO> vos = perms.stream().map(this::toVO).collect(Collectors.toList());
        return new PageImpl<>(vos, page, perms.getTotalElements());
    }

    @Override
    public PermissionVO createPermission(PermissionSaveDTO saveDTO) {
        Permission permission = new Permission();
        BeanUtils.copyProperties(saveDTO, permission);
        permissionRepository.save(permission);
        return toVO(permission);
    }

    @Override
    public PermissionVO updatePermission(PermissionSaveDTO saveDTO) {
        Permission permission = permissionRepository.findById(saveDTO.getId()).orElseThrow(() -> new RuntimeException("Permission not found"));
        BeanUtils.copyProperties(saveDTO, permission, "id", "createTime", "updateTime");
        return toVO(permissionRepository.save(permission));
    }

    @Override
    public void deletePermission(UUID id) {
        permissionRepository.deleteById(id);
    }

    @Override
    public void deletePermissionsBatch(List<UUID> ids) {
        permissionRepository.deleteAll(permissionRepository.findAllById(ids));
    }

    @Override
    public List<PermissionVO> fetchPermissionTree() {
        List<Permission> all = permissionRepository.findAll();
        return all.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public void updatePermissionStatus(UUID id, Boolean status) {
        permissionRepository.findById(id).ifPresent(p -> { p.setStatus(status); permissionRepository.save(p); });
    }

    private PermissionVO toVO(Permission permission) {
        PermissionVO vo = new PermissionVO();
        BeanUtils.copyProperties(permission, vo);
        return vo;
    }
}
