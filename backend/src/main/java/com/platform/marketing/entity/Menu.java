package com.platform.marketing.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "sys_menu")
public class Menu {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    private String name;

    @Column(name = "parent_id", length = 36)
    private String parentId;

    private String path;

    private String icon;

    @Column(name = "order_num")
    private Integer orderNum;

    private String permission;

    private Boolean status = true;

    private String type; // menu, button

    private String component;

    @Column(name = "keep_alive")
    private Boolean keepAlive;

    @Column(name = "is_external")
    private Boolean external;

    private Boolean hidden;

    private String remark;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Integer getOrderNum() { return orderNum; }
    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; }

    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }

    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }

    public Boolean getKeepAlive() { return keepAlive; }
    public void setKeepAlive(Boolean keepAlive) { this.keepAlive = keepAlive; }

    public Boolean getExternal() { return external; }
    public void setExternal(Boolean external) { this.external = external; }

    public Boolean getHidden() { return hidden; }
    public void setHidden(Boolean hidden) { this.hidden = hidden; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
