package com.platform.marketing.module.role.repository;

import com.platform.marketing.module.role.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Role.
 */
public interface RoleRepository extends JpaRepository<Role, UUID> {
    @Query("select r from Role r")
    Page<Role> findAllRoles(Pageable pageable);

    Optional<Role> findById(UUID id);
}
