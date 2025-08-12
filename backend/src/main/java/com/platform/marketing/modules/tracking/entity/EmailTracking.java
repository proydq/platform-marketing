package com.platform.marketing.modules.tracking.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "email_tracking")
public class EmailTracking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tracking_id", unique = true, nullable = false)
    private String trackingId; // 邮件唯一追踪ID
    
    @Column(name = "campaign_id")
    private Long campaignId; // 营销活动ID
    
    @Column(name = "customer_id")
    private Long customerId; // 客户ID
    
    @Column(name = "email_address", nullable = false)
    private String emailAddress;
    
    @Column(name = "subject")
    private String subject;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    @Column(name = "opened_at")
    private LocalDateTime openedAt;
    
    @Column(name = "first_click_at")
    private LocalDateTime firstClickAt;
    
    @Column(name = "replied_at")
    private LocalDateTime repliedAt;
    
    @Column(name = "bounced_at")
    private LocalDateTime bouncedAt;
    
    @Column(name = "unsubscribed_at")
    private LocalDateTime unsubscribedAt;
    
    @Column(name = "open_count")
    private Integer openCount = 0;
    
    @Column(name = "click_count")
    private Integer clickCount = 0;
    
    @Column(name = "status")
    private String status; // SENT, DELIVERED, OPENED, CLICKED, REPLIED, BOUNCED, UNSUBSCRIBED
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "device_type")
    private String deviceType; // DESKTOP, MOBILE, TABLET
    
    @Column(name = "location")
    private String location; // 地理位置信息
    
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