package com.platform.marketing.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "role_permission")
public class RolePermission {

    @EmbeddedId
    private RolePermissionId id;

    public RolePermission() {}

    public RolePermission(RolePermissionId id) {
        this.id = id;
    }

    public RolePermissionId getId() {
        return id;
    }

    public void setId(RolePermissionId id) {
        this.id = id;
    }
}
