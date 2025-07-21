package com.platform.marketing.module.role.controller;

import com.platform.marketing.module.auth.dto.Result;
import com.platform.marketing.module.role.dto.RoleQueryDTO;
import com.platform.marketing.module.role.dto.RoleSaveDTO;
import com.platform.marketing.module.role.dto.RoleVO;
import com.platform.marketing.module.role.service.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for role management.
 */
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Fetch roles.
     */
    @GetMapping
    public Result<Page<RoleVO>> fetchRoles(RoleQueryDTO queryDTO) {
        return Result.ok(roleService.fetchRoles(queryDTO));
    }

    /**
     * Get role detail.
     */
    @GetMapping("/{id}")
    public Result<RoleVO> getRole(@PathVariable UUID id) {
        return Result.ok(roleService.getRole(id));
    }

    /**
     * Create role.
     */
    @PostMapping
    public Result<RoleVO> createRole(@RequestBody RoleSaveDTO saveDTO) {
        return Result.ok(roleService.createRole(saveDTO));
    }

    /**
     * Update role.
     */
    @PostMapping("/update")
    public Result<RoleVO> updateRole(@RequestBody RoleSaveDTO saveDTO) {
        return Result.ok(roleService.updateRole(saveDTO));
    }

    /**
     * Delete role.
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return Result.ok(null);
    }

    /**
     * Bind permissions to role.
     */
    @PostMapping("/{id}/permissions")
    public Result<Void> bindPermissions(@PathVariable UUID id, @RequestBody List<UUID> permissionIds) {
        roleService.bindPermissions(id, permissionIds);
        return Result.ok(null);
    }

    /**
     * Fetch role permission ids.
     */
    @GetMapping("/{id}/permissions")
    public Result<List<UUID>> fetchRolePermissions(@PathVariable UUID id) {
        return Result.ok(roleService.fetchRolePermissions(id));
    }
}
