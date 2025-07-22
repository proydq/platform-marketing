package com.platform.marketing.auth;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

/**
 * Custom expression root providing {@code hasPermission(String)} support.
 */
public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;
    private Object target;

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    /**
     * Evaluate a permission against the current {@link Authentication}.
     *
     * @param permission permission identifier
     * @return {@code true} if the permission is granted
     */
    public boolean hasPermission(String permission) {
        if (permission == null) {
            return false;
        }
        return getPermissionEvaluator().hasPermission(getAuthentication(), null, permission);
    }

    // ------------------------------------------------------------------
    // Methods from {@link MethodSecurityExpressionOperations}
    // ------------------------------------------------------------------
    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public void setThis(Object target) {
        this.target = target;
    }
}
