package com.platform.marketing.controller;

import com.platform.marketing.dto.PermissionRequest;
import com.platform.marketing.entity.Role;
import com.platform.marketing.service.RoleService;
import com.platform.marketing.util.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.success(roleService.findAllRoles());
    }

    @GetMapping("/{id}/permissions")
    public ResponseEntity<List<String>> getRolePermissions(@PathVariable String id) {
        List<String> list = roleService.getPermissions(id);
        return ResponseEntity.success(list);
    }

    @PostMapping("/{id}/permissions")
    public ResponseEntity<Void> saveRolePermissions(@PathVariable String id, @RequestBody PermissionRequest request) {
        roleService.savePermissions(id, request.getPermissions());
        return ResponseEntity.success(null);
    }
}
