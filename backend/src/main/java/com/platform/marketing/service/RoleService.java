package com.platform.marketing.service;

import com.platform.marketing.entity.Permission;
import com.platform.marketing.entity.Role;
import com.platform.marketing.entity.RolePermission;
import com.platform.marketing.entity.RolePermissionId;
import com.platform.marketing.repository.PermissionRepository;
import com.platform.marketing.repository.RolePermissionRepository;
import com.platform.marketing.repository.RoleRepository;
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
    public void savePermissions(String roleId, List<String> codes) {
        rolePermissionRepository.deleteByIdRoleId(roleId);
        for (String code : codes) {
            Permission perm = permissionRepository.findByCode(code)
                    .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + code));
            RolePermissionId id = new RolePermissionId(roleId, perm.getId());
            RolePermission rp = new RolePermission(id);
            rolePermissionRepository.save(rp);
        }
    }
}
