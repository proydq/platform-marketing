package com.platform.marketing.auth;

import com.platform.marketing.entity.Role;
import com.platform.marketing.entity.User;
import com.platform.marketing.repository.RoleRepository;
import com.platform.marketing.service.RoleService;
import com.platform.marketing.service.UserService;
import com.platform.marketing.util.ResponseEntity;
import com.platform.marketing.auth.UserInfoDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    public AuthController(UserService userService,
                          RoleRepository roleRepository,
                          RoleService roleService) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoDTO> me(Authentication auth) {
        String username = auth.getName();
        User user = userService.findByUsername(username);
        java.util.List<String> roleIds = userService.getRoleIdsByUser(user.getId());
        String roleName = "";
        java.util.List<String> perms = new java.util.ArrayList<>();
        if (!roleIds.isEmpty()) {
            roleName = roleRepository.findById(roleIds.get(0))
                    .map(Role::getName).orElse("");
            for (String rid : roleIds) {
                perms.addAll(roleService.getPermissions(rid));
            }
        }
        UserInfoDTO dto = new UserInfoDTO();
        dto.setUsername(user.getUsername());
        dto.setRoleName(roleName);
        dto.setId(user.getId());
        dto.setPermissions(perms);
        return ResponseEntity.success(dto);
    }
}
