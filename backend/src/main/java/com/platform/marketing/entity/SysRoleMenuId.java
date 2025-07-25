package com.platform.marketing.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SysRoleMenuId implements Serializable {
    @Column(name = "role_id", length = 36)
    private String roleId;

    @Column(name = "menu_id", length = 36)
    private String menuId;

    public SysRoleMenuId() {}

    public SysRoleMenuId(String roleId, String menuId) {
        this.roleId = roleId;
        this.menuId = menuId;
    }

    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }

    public String getMenuId() { return menuId; }
    public void setMenuId(String menuId) { this.menuId = menuId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SysRoleMenuId that = (SysRoleMenuId) o;
        return Objects.equals(roleId, that.roleId) && Objects.equals(menuId, that.menuId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, menuId);
    }
}
