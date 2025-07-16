package com.platform.marketing.controller;

import com.platform.marketing.entity.Permission;
import com.platform.marketing.service.PermissionService;
import com.platform.marketing.util.ResponseEntity;
import com.platform.marketing.util.ResponsePageDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('permission:list')")
    public ResponseEntity<ResponsePageDataEntity<Permission>> list(@RequestParam(defaultValue = "") String keyword,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        Page<Permission> p = permissionService.search(keyword, PageRequest.of(page, size));
        return ResponseEntity.success(new ResponsePageDataEntity<>(p.getTotalElements(), p.getContent()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('permission:read')")
    public ResponseEntity<Permission> get(@PathVariable String id) {
        return permissionService.findById(id)
                .map(ResponseEntity::success)
                .orElse(ResponseEntity.fail(404, "Not Found"));
    }

    @PostMapping
    @PreAuthorize("hasPermission('permission:create')")
    public ResponseEntity<Permission> create(@RequestBody Permission permission) {
        return ResponseEntity.success(permissionService.create(permission));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('permission:update')")
    public ResponseEntity<Permission> update(@PathVariable String id, @RequestBody Permission permission) {
        return ResponseEntity.success(permissionService.update(id, permission));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('permission:delete')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        permissionService.delete(id);
        return ResponseEntity.success(null);
    }

    @DeleteMapping
    @PreAuthorize("hasPermission('permission:delete')")
    public ResponseEntity<Void> deleteBatch(@RequestBody List<String> ids) {
        permissionService.deleteBatch(ids);
        return ResponseEntity.success(null);
    }
}
