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
import com.platform.marketing.repository.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class SuperAdminInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminInitializer(RoleRepository roleRepository,
                                 PermissionRepository permissionRepository,
                                 RolePermissionRepository rolePermissionRepository,
                                 UserRepository userRepository,
                                 UserRoleRepository userRoleRepository,
                                 PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
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

        if (permissionRepository.count() == 0) {
            Permission p1 = new Permission();
            p1.setId(UUID.randomUUID().toString());
            p1.setCode("permission:list");
            p1.setName("Permission List");
            permissionRepository.save(p1);

            Permission p2 = new Permission();
            p2.setId(UUID.randomUUID().toString());
            p2.setCode("user:add");
            p2.setName("Add User");
            permissionRepository.save(p2);

            Permission p3 = new Permission();
            p3.setId(UUID.randomUUID().toString());
            p3.setCode("role:view");
            p3.setName("View Role");
            permissionRepository.save(p3);
            log.info("Inserted default permissions");
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
            admin.setPassword(passwordEncoder.encode("123456"));
            userRepository.save(admin);
            userRoleRepository.save(new com.platform.marketing.entity.UserRole(
                    new com.platform.marketing.entity.UserRoleId(admin.getId(), role.getId())));
            log.info("Created admin user");
        }
    }
}
