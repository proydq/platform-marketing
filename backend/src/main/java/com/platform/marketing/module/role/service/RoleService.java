package com.platform.marketing.module.role.service;

import com.platform.marketing.module.role.dto.RoleQueryDTO;
import com.platform.marketing.module.role.dto.RoleSaveDTO;
import com.platform.marketing.module.role.dto.RoleVO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Role service interface.
 */
public interface RoleService {
    Page<RoleVO> fetchRoles(RoleQueryDTO queryDTO);

    RoleVO getRole(UUID id);

    RoleVO createRole(RoleSaveDTO saveDTO);

    RoleVO updateRole(RoleSaveDTO saveDTO);

    void deleteRole(UUID id);

    void bindPermissions(UUID id, List<UUID> permissionIds);

    List<UUID> fetchRolePermissions(UUID id);
}
