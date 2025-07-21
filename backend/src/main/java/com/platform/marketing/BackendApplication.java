package com.platform.marketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.boot.CommandLineRunner;

import com.platform.marketing.MethodSecurityConfig;
import com.platform.marketing.entity.User;
import com.platform.marketing.entity.Role;
import com.platform.marketing.repository.UserRepository;
import com.platform.marketing.repository.RoleRepository;
import com.platform.marketing.repository.PermissionRepository;

import java.util.List;

@SpringBootApplication
@Import(MethodSecurityConfig.class)
public class BackendApplication implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public BackendApplication(UserRepository userRepository,
                              RoleRepository roleRepository,
                              PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("\uD83D\uDC4B Checking admin user status...");

        userRepository.findByUsername("admin").ifPresentOrElse(admin -> {
            System.out.println("Admin user found: " + admin.getUsername());

            if (admin.getRoleId() == null) {
                System.out.println("\u274C admin roleId missing");
                return;
            }

            Role role = roleRepository.findById(admin.getRoleId()).orElse(null);
            if (role == null) {
                System.out.println("\u274C admin role not found");
            } else {
                System.out.println("Admin role: " + role.getName());
                List<String> perms = permissionRepository.findCodesByRoleId(role.getId());
                if (perms.isEmpty()) {
                    System.out.println("\u274C admin has no permissions");
                } else {
                    System.out.println("Admin permissions: " + perms);
                }
            }
        }, () -> System.out.println("\u274C admin user not found"));
    }
}
