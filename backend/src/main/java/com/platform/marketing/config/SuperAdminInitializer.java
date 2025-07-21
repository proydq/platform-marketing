package com.platform.marketing.config;

import com.platform.marketing.entity.Role;
import com.platform.marketing.entity.Permission;
import com.platform.marketing.entity.RolePermission;
import com.platform.marketing.entity.RolePermissionId;
import com.platform.marketing.entity.User;
import com.platform.marketing.repository.RoleRepository;
import com.platform.marketing.repository.PermissionRepository;
import com.platform.marketing.repository.RolePermissionRepository;
import com.platform.marketing.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
// @Component
public class SuperAdminInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminInitializer(RoleRepository roleRepository,
                                 PermissionRepository permissionRepository,
                                 RolePermissionRepository rolePermissionRepository,
                                 UserRepository userRepository,
                                 PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        final String roleName = "超级管理员";
        Role role = roleRepository.findByName(roleName).orElse(null);
        if (role == null) {
            role = new Role();
            role.setId(UUID.randomUUID().toString());
            role.setName(roleName);
            role.setDescription("拥有所有权限");
            roleRepository.save(role);
            log.info("Created role {}", roleName);
        }

        if (!permissionRepository.existsByCode("test:hello")) {
            Permission perm = new Permission();
            perm.setId(UUID.randomUUID().toString());
            perm.setCode("test:hello");
            perm.setName("Test Hello");
            perm.setDescription("test hello permission");
            permissionRepository.save(perm);
            log.info("Created permission test:hello");
        }

        List<RolePermission> existing = rolePermissionRepository.findByIdRoleId(role.getId());
        if (existing.isEmpty()) {
            List<Permission> permissions = permissionRepository.findAll();
            for (Permission p : permissions) {
                RolePermissionId id = new RolePermissionId(role.getId(), p.getId());
                if (!rolePermissionRepository.existsById(id)) {
                    rolePermissionRepository.save(new RolePermission(id));
                }
            }
            log.info("Assigned {} permissions to role {}", permissions.size(), roleName);
        }

        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setId(UUID.randomUUID().toString());
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoleId(role.getId());
            userRepository.save(admin);
            log.info("Created admin user");
        }
    }
}
