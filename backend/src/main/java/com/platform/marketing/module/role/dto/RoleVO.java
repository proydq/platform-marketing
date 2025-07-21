package com.platform.marketing.module.role.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Role view object.
 */
public class RoleVO {
    private UUID id;
    private String name;
    private String code;
    private String description;
    private Boolean status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Set<UUID> permissionIds;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Set<UUID> getPermissionIds() { return permissionIds; }
    public void setPermissionIds(Set<UUID> permissionIds) { this.permissionIds = permissionIds; }
}
