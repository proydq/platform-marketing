package com.platform.marketing.repository;

import com.platform.marketing.entity.UserRole;
import com.platform.marketing.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    List<UserRole> findByIdUserId(String userId);
    List<UserRole> findByIdRoleId(String roleId);
    List<UserRole> findByIdRoleIdIn(java.util.Collection<String> roleIds);
    void deleteByIdUserId(String userId);
}
