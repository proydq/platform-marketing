package com.platform.marketing.repository;

import com.platform.marketing.entity.RolePermission;
import com.platform.marketing.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
    List<RolePermission> findByIdRoleId(String roleId);
    void deleteByIdRoleId(String roleId);
}
