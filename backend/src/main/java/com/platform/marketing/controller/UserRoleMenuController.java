package com.platform.marketing.controller;

import com.platform.marketing.entity.Menu;
import com.platform.marketing.service.RoleMenuService;
import com.platform.marketing.service.UserService;
import com.platform.marketing.util.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserRoleMenuController {

    private final UserService userService;
    private final RoleMenuService roleMenuService;

    public UserRoleMenuController(UserService userService, RoleMenuService roleMenuService) {
        this.userService = userService;
        this.roleMenuService = roleMenuService;
    }

    @PostMapping("/users/{userId}/roles")
    @PreAuthorize("hasPermission('user:update')")
    public ResponseEntity<Void> assignRolesToUser(@PathVariable String userId, @RequestBody List<String> roleIds) {
        userService.assignRoles(userId, roleIds);
        return ResponseEntity.success(null);
    }

    @GetMapping("/users/{userId}/permissions")
    @PreAuthorize("hasPermission('user:view')")
    public ResponseEntity<List<Menu>> getUserPermissions(@PathVariable String userId) {
        return ResponseEntity.success(roleMenuService.getMenusByUser(userId));
    }

    @PostMapping("/roles/{roleId}/menus")
    @PreAuthorize("hasPermission('role:update')")
    public ResponseEntity<Void> assignMenusToRole(@PathVariable String roleId, @RequestBody List<String> menuIds) {
        roleMenuService.assignMenusToRole(roleId, menuIds);
        return ResponseEntity.success(null);
    }
}
