package com.platform.marketing.module.permission.controller;

import com.platform.marketing.module.auth.dto.Result;
import com.platform.marketing.module.permission.dto.PermissionQueryDTO;
import com.platform.marketing.module.permission.dto.PermissionSaveDTO;
import com.platform.marketing.module.permission.dto.PermissionVO;
import com.platform.marketing.module.permission.service.PermissionService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for permission management.
 */
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * Fetch permissions.
     */
    @GetMapping
    public Result<Page<PermissionVO>> fetchPermissions(PermissionQueryDTO queryDTO) {
        return Result.ok(permissionService.fetchPermissions(queryDTO));
    }

    /**
     * Create permission.
     */
    @PostMapping
    public Result<PermissionVO> createPermission(@RequestBody PermissionSaveDTO saveDTO) {
        return Result.ok(permissionService.createPermission(saveDTO));
    }

    /**
     * Update permission.
     */
    @PostMapping("/update")
    public Result<PermissionVO> updatePermission(@RequestBody PermissionSaveDTO saveDTO) {
        return Result.ok(permissionService.updatePermission(saveDTO));
    }

    /**
     * Delete permission.
     */
    @DeleteMapping("/{id}")
    public Result<Void> deletePermission(@PathVariable UUID id) {
        permissionService.deletePermission(id);
        return Result.ok(null);
    }

    /**
     * Delete permissions in batch.
     */
    @PostMapping("/delete-batch")
    public Result<Void> deletePermissionsBatch(@RequestBody List<UUID> ids) {
        permissionService.deletePermissionsBatch(ids);
        return Result.ok(null);
    }

    /**
     * Get permission tree.
     */
    @GetMapping("/tree")
    public Result<List<PermissionVO>> fetchPermissionTree() {
        return Result.ok(permissionService.fetchPermissionTree());
    }

    /**
     * Update permission status.
     */
    @PostMapping("/update-status")
    public Result<Void> updatePermissionStatus(@RequestParam UUID id, @RequestParam Boolean status) {
        permissionService.updatePermissionStatus(id, status);
        return Result.ok(null);
    }
}
