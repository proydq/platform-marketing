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
           "AND (:status IS NULL OR p.status = :status)")
    Page<Permission> search(@Param("keyword") String keyword,
                            @Param("type") String type,
                            @Param("status") Boolean status,
                            Pageable pageable);
}
