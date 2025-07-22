package com.platform.marketing.config;

import com.platform.marketing.auth.CustomPermissionEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MethodSecurityConfig.class);

    private final CustomPermissionEvaluator permissionEvaluator;

    public MethodSecurityConfig(CustomPermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
        log.info("\uD83D\uDFE2 MethodSecurityConfig 初始化成功！");
    }

    @Override
    protected DefaultMethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(permissionEvaluator);
        return handler;
    }
}
