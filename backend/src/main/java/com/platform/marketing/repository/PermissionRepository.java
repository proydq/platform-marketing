package com.platform.marketing.repository;

import com.platform.marketing.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
    Optional<Permission> findByCode(String code);
    boolean existsByCode(String code);

    Page<Permission> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name,
                                                                             String code,
                                                                             Pageable pageable);
}
