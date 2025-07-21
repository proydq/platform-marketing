package com.platform.marketing.module.permission.repository;

import com.platform.marketing.module.permission.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Permission.
 */
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    @Query("select p from Permission p where (:type is null or p.type=:type) and (:status is null or p.status=:status) and (:kw is null or p.name like %:kw% or p.code like %:kw%)")
    Page<Permission> search(@Param("kw") String keyword, @Param("type") String type, @Param("status") Boolean status, Pageable pageable);

    List<Permission> findByParentId(UUID parentId);
}
