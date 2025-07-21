package com.platform.marketing.module.permission.service;

import com.platform.marketing.module.permission.dto.PermissionQueryDTO;
import com.platform.marketing.module.permission.dto.PermissionSaveDTO;
import com.platform.marketing.module.permission.dto.PermissionVO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Permission service interface.
 */
public interface PermissionService {
    Page<PermissionVO> fetchPermissions(PermissionQueryDTO queryDTO);

    PermissionVO createPermission(PermissionSaveDTO saveDTO);

    PermissionVO updatePermission(PermissionSaveDTO saveDTO);

    void deletePermission(UUID id);

    void deletePermissionsBatch(List<UUID> ids);

    List<PermissionVO> fetchPermissionTree();

    void updatePermissionStatus(UUID id, Boolean status);
}
