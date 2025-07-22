package com.platform.marketing.auth;

import com.platform.marketing.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    private final PermissionRepository permissionRepository;

    public AuthService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public List<String> getPermissionsByUsername(String username) {
        return permissionRepository.findPermissionCodesByUsername(username);
    }
}
