package com.platform.marketing.controller;

import com.platform.marketing.entity.Menu;
import com.platform.marketing.entity.User;
import com.platform.marketing.entity.Role;
import com.platform.marketing.service.RoleMenuService;
import com.platform.marketing.service.UserService;
import com.platform.marketing.service.RoleService;
import com.platform.marketing.util.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserRoleMenuController {

    private final UserService userService;
    private final RoleMenuService roleMenuService;
    private final RoleService roleService;

    public UserRoleMenuController(UserService userService, RoleMenuService roleMenuService, RoleService roleService) {
        this.userService = userService;
        this.roleMenuService = roleMenuService;
        this.roleService = roleService;
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

    @GetMapping("/menus/{menuId}/users")
    @PreAuthorize("hasPermission('menu:view')")
    public ResponseEntity<List<User>> getUsersByMenu(@PathVariable String menuId) {
        return ResponseEntity.success(roleMenuService.getUsersByMenu(menuId));
    }

    @GetMapping("/menus/{menuId}/roles")
    @PreAuthorize("hasPermission('menu:view')")
    public ResponseEntity<List<Role>> getRolesByMenu(@PathVariable String menuId) {
        return ResponseEntity.success(roleMenuService.getRolesByMenu(menuId));
    }

    @PostMapping("/menus/{menuId}/roles")
    @PreAuthorize("hasPermission('menu:update')")
    public ResponseEntity<Void> assignRolesToMenu(@PathVariable String menuId, @RequestBody List<String> roleIds) {
        roleMenuService.assignRolesToMenu(menuId, roleIds);
        return ResponseEntity.success(null);
    }

    @GetMapping("/roles/all")
    @PreAuthorize("hasPermission('role:view')")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.success(roleService.findAllRoles());
    }
}
