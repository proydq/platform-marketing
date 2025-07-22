package com.platform.marketing.controller;

import com.platform.marketing.entity.User;
import com.platform.marketing.service.UserService;
import com.platform.marketing.util.ResponseEntity;
import com.platform.marketing.util.ResponsePageDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasPermission('user:list')")
    public ResponseEntity<ResponsePageDataEntity<User>> list(@RequestParam(defaultValue = "") String keyword,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        Page<User> p = userService.search(keyword, PageRequest.of(page, size));
        return ResponseEntity.success(new ResponsePageDataEntity<>(p.getTotalElements(), p.getContent()));
    }

    @PostMapping
    @PreAuthorize("hasPermission('user:create')")
    public ResponseEntity<User> create(@RequestBody User user) {
        return ResponseEntity.success(userService.create(user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('user:update')")
    public ResponseEntity<User> update(@PathVariable String id, @RequestBody User user) {
        user.setId(id);
        return ResponseEntity.success(userService.update(user));
    }

    @PostMapping("/update-status")
    @PreAuthorize("hasPermission('user:update')")
    public ResponseEntity<Void> updateStatus(@RequestBody java.util.Map<String, Object> body) {
        String id = (String) body.get("id");
        Boolean status = (Boolean) body.get("status");
        if (id == null || status == null) {
            return ResponseEntity.fail(400, "id and status required");
        }
        userService.updateStatus(id, status);
        return ResponseEntity.success(null);
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasPermission('user:reset-password')")
    public ResponseEntity<Void> resetPassword(@PathVariable String id, @RequestBody java.util.Map<String, String> body) {
        String password = body.get("password");
        if (password == null) {
            return ResponseEntity.fail(400, "password required");
        }
        userService.resetPassword(id, password);
        return ResponseEntity.success(null);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('user:delete')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.success(null);
    }

    @GetMapping("/{id}/roles")
    @PreAuthorize("hasPermission('user:view')")
    public ResponseEntity<java.util.List<String>> getUserRoles(@PathVariable String id) {
        java.util.List<String> roleIds = userService.getRoleIdsByUser(id);
        return ResponseEntity.success(roleIds);
    }

    @PostMapping("/{id}/roles")
    @PreAuthorize("hasPermission('user:update')")
    public ResponseEntity<Void> assignRoles(@PathVariable String id, @RequestBody java.util.List<String> roleIds) {
        userService.assignRoles(id, roleIds);
        return ResponseEntity.success(null);
    }
}
