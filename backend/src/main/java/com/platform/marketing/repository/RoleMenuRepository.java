package com.platform.marketing.repository;

import com.platform.marketing.entity.SysRoleMenu;
import com.platform.marketing.entity.SysRoleMenuId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface RoleMenuRepository extends JpaRepository<SysRoleMenu, SysRoleMenuId> {
    List<SysRoleMenu> findByIdRoleId(String roleId);
    List<SysRoleMenu> findByIdMenuId(String menuId);
    void deleteByIdRoleId(String roleId);
    void deleteByIdMenuId(String menuId);

    @Modifying
    @Transactional
    @Query("DELETE FROM SysRoleMenu rm WHERE rm.id.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") String roleId);

    @Modifying
    @Transactional
    @Query("DELETE FROM SysRoleMenu rm WHERE rm.id.menuId = :menuId")
    void deleteByMenuId(@Param("menuId") String menuId);
}
