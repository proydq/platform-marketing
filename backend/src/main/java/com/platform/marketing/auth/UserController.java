package com.platform.marketing.auth;

import com.platform.marketing.entity.Role;
import com.platform.marketing.entity.User;
import com.platform.marketing.repository.RoleRepository;
import com.platform.marketing.service.RoleService;
import com.platform.marketing.service.UserService;
import com.platform.marketing.util.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("authUserController")
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    public UserController(UserService userService,
                          RoleRepository roleRepository,
                          RoleService roleService) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Authentication auth) {
        String username = auth.getName();
        User user = userService.findByUsername(username);
        java.util.List<String> roleIds = userService.getRoleIdsByUser(user.getId());
        String roleName = "";
        java.util.List<String> perms = java.util.Collections.emptyList();
        if (!roleIds.isEmpty()) {
            String rid = roleIds.get(0);
            roleName = roleRepository.findById(rid)
                    .map(Role::getName).orElse("");
            perms = roleService.getPermissions(rid);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("roleName", roleName);
        map.put("permissions", perms);
        return ResponseEntity.success(map);
    }
}
