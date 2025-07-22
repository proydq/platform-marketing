package com.platform.marketing.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import com.platform.marketing.auth.CustomPermissionEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MethodSecurityConfig.class);

    private final PermissionEvaluator permissionEvaluator;

    public MethodSecurityConfig(PermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
        log.info("\uD83D\uDFE2 MethodSecurityConfig 初始化了！");
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(permissionEvaluator);
        return handler;
    }
}
