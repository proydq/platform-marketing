package com.platform.marketing.controller;

import com.platform.marketing.entity.Permission;
import com.platform.marketing.service.PermissionService;
import com.platform.marketing.util.ResponseEntity;
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
    public ResponseEntity<List<Permission>> list() {
        return ResponseEntity.success(permissionService.findAll());
    }

    @PostMapping
    public ResponseEntity<Permission> create(@RequestBody Permission permission) {
        return ResponseEntity.success(permissionService.create(permission));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Permission> update(@PathVariable String id, @RequestBody Permission permission) {
        return ResponseEntity.success(permissionService.update(id, permission));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        permissionService.delete(id);
        return ResponseEntity.success(null);
    }
}
