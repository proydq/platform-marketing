package com.platform.marketing.config;

import com.platform.marketing.auth.CustomMethodSecurityExpressionHandler;
import com.platform.marketing.auth.CustomPermissionEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Bean
    public CustomPermissionEvaluator customPermissionEvaluator() {
        return new CustomPermissionEvaluator();
    }

    @Override
    protected CustomMethodSecurityExpressionHandler createExpressionHandler() {
        CustomMethodSecurityExpressionHandler expressionHandler =
            new CustomMethodSecurityExpressionHandler(customPermissionEvaluator());
        expressionHandler.setPermissionEvaluator(customPermissionEvaluator());
        return expressionHandler;
    }
}
