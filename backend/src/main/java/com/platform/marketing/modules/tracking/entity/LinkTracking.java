package com.platform.marketing.modules.tracking.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "link_tracking")
public class LinkTracking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tracking_id", nullable = false)
    private String trackingId; // 关联邮件追踪ID
    
    @Column(name = "original_url", length = 1000, nullable = false)
    private String originalUrl; // 原始链接
    
    @Column(name = "short_url", unique = true, nullable = false)
    private String shortUrl; // 短链接标识符
    
    @Column(name = "clicked_at")
    private LocalDateTime clickedAt;
    
    @Column(name = "click_count")
    private Integer clickCount = 0;
    
    @Column(name = "unique_clicks")
    private Integer uniqueClicks = 0; // 去重点击数
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "referrer", length = 500)
    private String referrer;
    
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