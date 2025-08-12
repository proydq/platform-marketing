package com.platform.marketing.modules.ai.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "message_templates")
public class MessageTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String type; // EMAIL, WHATSAPP, LINKEDIN
    
    @Column(nullable = false)
    private String industry; // 适用行业
    
    @Column(nullable = false)
    private String language; // 语言代码: en, es, fr, zh等
    
    @Column(nullable = false, length = 500)
    private String subject; // 邮件主题或消息标题
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 模板内容，支持变量占位符
    
    @Column(columnDefinition = "TEXT")
    private String variables; // JSON格式的变量说明
    
    private String category; // 模板分类：开发信、跟进、产品推广等
    
    private Boolean isActive = true;
    
    @Column(name = "usage_count")
    private Integer usageCount = 0;
    
    @Column(name = "success_rate")
    private Double successRate = 0.0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}