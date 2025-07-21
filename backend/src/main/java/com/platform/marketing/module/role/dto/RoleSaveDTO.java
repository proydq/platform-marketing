package com.platform.marketing.module.role.dto;

import java.util.UUID;

/**
 * Save DTO for roles.
 */
public class RoleSaveDTO {
    private UUID id;
    private String name;
    private String code;
    private String description;
    private Boolean status;

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
}
