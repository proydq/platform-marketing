package com.platform.marketing.module.user.controller;

import com.platform.marketing.module.auth.dto.Result;
import com.platform.marketing.module.user.dto.UserQueryDTO;
import com.platform.marketing.module.user.dto.UserSaveDTO;
import com.platform.marketing.module.user.dto.UserVO;
import com.platform.marketing.module.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Controller for user management.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Fetch users with pagination.
     */
    @GetMapping
    public Result<Page<UserVO>> fetchUsers(UserQueryDTO queryDTO) {
        return Result.ok(userService.fetchUsers(queryDTO));
    }

    /**
     * Create new user.
     */
    @PostMapping
    public Result<UserVO> createUser(@RequestBody UserSaveDTO saveDTO) {
        return Result.ok(userService.createUser(saveDTO));
    }

    /**
     * Update user.
     */
    @PostMapping("/update")
    public Result<UserVO> updateUser(@RequestBody UserSaveDTO saveDTO) {
        return Result.ok(userService.updateUser(saveDTO));
    }

    /**
     * Delete user by id.
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return Result.ok(null);
    }

    /**
     * Import users by file.
     */
    @PostMapping("/import")
    public Result<Void> importUsers(@RequestParam("file") MultipartFile file) {
        userService.importUsers(file);
        return Result.ok(null);
    }

    /**
     * Update user status.
     */
    @PostMapping("/update-status")
    public Result<Void> updateUserStatus(@RequestParam UUID id, @RequestParam Boolean status) {
        userService.updateUserStatus(id, status);
        return Result.ok(null);
    }

    /**
     * Reset user password.
     */
    @PostMapping("/{id}/reset-password")
    public Result<Void> resetUserPassword(@PathVariable UUID id) {
        userService.resetPassword(id);
        return Result.ok(null);
    }
}
