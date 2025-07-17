package com.platform.marketing.controller;

import com.platform.marketing.entity.User;
import com.platform.marketing.service.UserService;
import com.platform.marketing.util.ResponseEntity;
import com.platform.marketing.util.ResponsePageDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ResponsePageDataEntity<User>> list(@RequestParam(defaultValue = "") String keyword,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        Page<User> p = userService.search(keyword, PageRequest.of(page, size));
        return ResponseEntity.success(new ResponsePageDataEntity<>(p.getTotalElements(), p.getContent()));
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        return ResponseEntity.success(userService.create(user));
    }

    @PostMapping("/update")
    public ResponseEntity<User> update(@RequestBody User user) {
        return ResponseEntity.success(userService.update(user.getId(), user));
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable String id) {
        userService.resetPassword(id);
        return ResponseEntity.success(null);
    }

    @PostMapping("/update-status")
    public ResponseEntity<Void> updateStatus(@RequestBody java.util.Map<String, Object> body) {
        String id = (String) body.get("id");
        Boolean status = (Boolean) body.get("status");
        if (id == null || status == null) {
            return ResponseEntity.fail(400, "id and status required");
        }
        userService.updateStatus(id, status);
        return ResponseEntity.success(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.success(null);
    }
}
