package com.platform.marketing.service;

import com.platform.marketing.dto.RoleDto;
import com.platform.marketing.entity.Permission;
import com.platform.marketing.entity.Role;
import com.platform.marketing.entity.RolePermission;
import com.platform.marketing.entity.RolePermissionId;
import com.platform.marketing.repository.PermissionRepository;
import com.platform.marketing.repository.RolePermissionRepository;
import com.platform.marketing.repository.RoleRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public RoleService(RoleRepository roleRepository,
                       PermissionRepository permissionRepository,
                       RolePermissionRepository rolePermissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    public Role create(Role role) {
        return roleRepository.save(role);
    }

    public Role update(String id, Role role) {
        Role existing = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        existing.setName(role.getName());
        existing.setDescription(role.getDescription());
        existing.setStatus(role.isStatus());
        return roleRepository.save(existing);
    }

    public void delete(String id) {
        roleRepository.deleteById(id);
    }

    public List<String> getPermissions(String roleId) {
        if (roleId == null) {
            return java.util.Collections.emptyList();
        }
        return permissionRepository.findCodesByRoleId(roleId);
    }

    @Transactional
    public void savePermissions(String roleId, List<String> permissionIds) {
        rolePermissionRepository.deleteByIdRoleId(roleId);
        for (String permissionId : permissionIds) {
            Permission perm = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionId));
            RolePermissionId id = new RolePermissionId(roleId, perm.getId());
            RolePermission rp = new RolePermission(id);
            rolePermissionRepository.save(rp);
        }
    }

    @Transactional
    public RoleDto createRoleWithPermissions(RoleDto dto) {
        Role role = new Role();
        role.setId(UUID.randomUUID().toString());
        RoleDto.copyToEntity(dto, role);
        role.setStatus(true);
        roleRepository.save(role);

        if (dto.getPermissionIds() != null) {
            for (String permId : dto.getPermissionIds()) {
                Permission perm = permissionRepository.findById(permId)
                        .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permId));
                RolePermissionId id = new RolePermissionId(role.getId(), perm.getId());
                rolePermissionRepository.save(new RolePermission(id));
            }
        }

        RoleDto result = RoleDto.fromEntity(role);
        result.setPermissionIds(dto.getPermissionIds());
        return result;
    }

    @Transactional
    public RoleDto updateRoleWithPermissions(RoleDto dto) {
        Role role = roleRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        RoleDto.copyToEntity(dto, role);
        roleRepository.save(role);

        rolePermissionRepository.deleteByRoleId(role.getId());
        if (dto.getPermissionIds() != null) {
            for (String permId : dto.getPermissionIds()) {
                Permission perm = permissionRepository.findById(permId)
                        .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permId));
                RolePermissionId id = new RolePermissionId(role.getId(), perm.getId());
                rolePermissionRepository.save(new RolePermission(id));
            }
        }

        RoleDto result = RoleDto.fromEntity(role);
        result.setPermissionIds(dto.getPermissionIds());
        return result;
    }
}
