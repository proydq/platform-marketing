package com.platform.marketing.module.permission.dto;

import java.util.UUID;

/**
 * Save DTO for permissions.
 */
public class PermissionSaveDTO {
    private UUID id;
    private String name;
    private String code;
    private String type;
    private Boolean status;
    private UUID parentId;
    private Integer sort;
    private String remark;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
    public UUID getParentId() { return parentId; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
