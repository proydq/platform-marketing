package com.platform.marketing.auth;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;

import org.springframework.security.core.Authentication;

/**
 * Custom expression root exposing {@code hasPermission(String)} to SpEL.
 */
public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot
        implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;
    private Object target;


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

    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public void setThis(Object target) {
        this.target = target;
    }

    @Override
    public Object getFilterObject() {
        return filterObject;
    }

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

}
