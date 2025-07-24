package com.platform.marketing.dto;

import com.platform.marketing.entity.Role;
import java.util.List;

public class RoleDto {
    private String id;
    private String name;
    private String description;
    private boolean status = true;
    private List<String> permissionIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<String> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<String> permissionIds) {
        this.permissionIds = permissionIds;
    }

    public static RoleDto fromEntity(Role role) {
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setStatus(role.isStatus());
        return dto;
    }

    public static void copyToEntity(RoleDto dto, Role role) {
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setStatus(dto.isStatus());
    }
}
