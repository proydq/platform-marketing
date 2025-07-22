package com.platform.marketing.auth;

import org.springframework.security.access.expression.method.MethodSecurityExpressionRoot;
import org.springframework.security.core.Authentication;

/**
 * Custom expression root exposing {@code hasPermission(String)} to SpEL.
 */
public class CustomMethodSecurityExpressionRoot extends MethodSecurityExpressionRoot {

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    /**
     * Check permission string using the configured {@code PermissionEvaluator}.
     *
     * @param permission permission name
     * @return {@code true} if the current user has the permission
     */
    public boolean hasPermission(String permission) {
        return permission != null && hasPermission((Object) null, permission);
    }
}
