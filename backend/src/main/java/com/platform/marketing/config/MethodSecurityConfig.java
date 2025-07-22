package com.platform.marketing.config;

import com.platform.marketing.auth.CustomPermissionEvaluator;
import com.platform.marketing.auth.CustomMethodSecurityExpressionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    private final CustomPermissionEvaluator permissionEvaluator;
    private final CustomMethodSecurityExpressionHandler expressionHandler;

    public MethodSecurityConfig(CustomPermissionEvaluator permissionEvaluator,
                               CustomMethodSecurityExpressionHandler expressionHandler) {
        this.permissionEvaluator = permissionEvaluator;
        this.expressionHandler = expressionHandler;
    }

    @Override
    protected CustomMethodSecurityExpressionHandler createExpressionHandler() {
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        return expressionHandler;
    }
}
