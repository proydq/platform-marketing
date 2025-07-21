package com.platform.marketing.auth;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private static final Logger log = LoggerFactory.getLogger(CustomPermissionEvaluator.class);

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || permission == null) {
            return false;
        }
        String perm = permission.toString();
        log.info("\uD83D\uDD10 正在验证权限: {}", perm);
        boolean result = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(perm::equals);
        log.info("\u2705 验证结果: {}", result);

        return result;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return hasPermission(authentication, targetType, permission);
    }
}
