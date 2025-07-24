package com.platform.marketing.repository;

import com.platform.marketing.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
    Optional<Permission> findByCode(String code);
    boolean existsByCode(String code);

    @Query("SELECT p.code FROM Permission p JOIN RolePermission rp ON p.id = rp.id.permissionId WHERE rp.id.roleId = :roleId")
    java.util.List<String> findCodesByRoleId(@Param("roleId") String roleId);

    Page<Permission> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name,
                                                                             String code,
                                                                             Pageable pageable);

    @Query("SELECT p FROM Permission p " +
           "WHERE (:keyword = '' OR lower(p.name) LIKE lower(concat('%', :keyword, '%')) " +
           "OR lower(p.code) LIKE lower(concat('%', :keyword, '%'))) " +
           "AND (:type = '' OR p.type = :type) " +
           "AND (:module = '' OR lower(p.module) LIKE lower(concat('%', :module, '%'))) " +
           "AND (:status IS NULL OR p.status = :status)")
    Page<Permission> search(@Param("keyword") String keyword,
                            @Param("type") String type,
                            @Param("module") String module,
                            @Param("status") Boolean status,
                            Pageable pageable);

    @Query(value = "SELECT DISTINCT p.code FROM sys_user u " +
            "JOIN user_role ur ON u.id = ur.user_id " +
            "JOIN role_permission rp ON ur.role_id = rp.role_id " +
            "JOIN sys_permission p ON rp.permission_id = p.id " +
            "WHERE u.username = :username",
            nativeQuery = true)
    java.util.List<String> findPermissionCodesByUsername(@Param("username") String username);
}
