package com.platform.marketing.module.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User view object.
 */
public class UserVO {
    private UUID id;
    private String username;
    private String email;
    private Boolean status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
