package com.platform.marketing.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "role_menu")
public class SysRoleMenu {

    @EmbeddedId
    private SysRoleMenuId id;

    public SysRoleMenu() {}

    public SysRoleMenu(SysRoleMenuId id) {
        this.id = id;
    }

    public SysRoleMenuId getId() {
        return id;
    }

    public void setId(SysRoleMenuId id) {
        this.id = id;
    }
}
