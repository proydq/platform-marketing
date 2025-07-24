package com.platform.marketing.system.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_settings")
public class SystemSettings {

    @Id
    @Column(length = 36)
    private String id;

    @Lob
    @Column(name = "basic_settings", columnDefinition = "text")
    private String basicSettings;

    @Lob
    @Column(name = "notification_settings", columnDefinition = "text")
    private String notificationSettings;

    @Lob
    @Column(name = "security_settings", columnDefinition = "text")
    private String securitySettings;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() { this.updatedAt = LocalDateTime.now(); }

    @PreUpdate
    public void preUpdate() { this.updatedAt = LocalDateTime.now(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBasicSettings() { return basicSettings; }
    public void setBasicSettings(String basicSettings) { this.basicSettings = basicSettings; }

    public String getNotificationSettings() { return notificationSettings; }
    public void setNotificationSettings(String notificationSettings) { this.notificationSettings = notificationSettings; }

    public String getSecuritySettings() { return securitySettings; }
    public void setSecuritySettings(String securitySettings) { this.securitySettings = securitySettings; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
