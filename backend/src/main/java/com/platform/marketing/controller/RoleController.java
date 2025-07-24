package com.platform.marketing.controller;

import com.platform.marketing.dto.PermissionRequest;
import com.platform.marketing.dto.RoleDto;
import com.platform.marketing.entity.Role;
import com.platform.marketing.service.RoleService;
import com.platform.marketing.util.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('role:view')")
    public ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.success(roleService.findAllRoles());
    }

    @PostMapping
    @PreAuthorize("hasPermission('role:create')")
    public ResponseEntity<RoleDto> create(@RequestBody RoleDto dto) {
        RoleDto created = roleService.createRoleWithPermissions(dto);
        return ResponseEntity.success(created);
    }

    @PostMapping("/update")
    @PreAuthorize("hasPermission('role:update')")
    public ResponseEntity<RoleDto> updateRole(@RequestBody RoleDto dto) {
        RoleDto updated = roleService.updateRoleWithPermissions(dto);
        return ResponseEntity.success(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('role:delete')")
    public ResponseEntity<Void> deleteRole(@PathVariable String id) {
        roleService.delete(id);
        return ResponseEntity.success(null);
    }

    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasPermission('role:view')")
    public ResponseEntity<List<String>> getRolePermissions(@PathVariable String id) {
        List<String> list = roleService.getPermissions(id);
        return ResponseEntity.success(list);
    }

    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasPermission('role:update')")
    public ResponseEntity<Void> saveRolePermissions(@PathVariable String id, @RequestBody PermissionRequest request) {
        roleService.savePermissions(id, request.getPermissions());
        return ResponseEntity.success(null);
    }
}


