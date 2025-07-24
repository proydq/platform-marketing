package com.platform.marketing.dto;

import java.util.ArrayList;
import java.util.List;

public class PermissionTreeNode {
    private String id;
    private String name;
    private String code;
    private String type;
    private String parentId;
    private String url;
    private String method;
    private boolean status;
    private String description;
    private String module;
    private List<PermissionTreeNode> children = new ArrayList<>();

    public PermissionTreeNode() {}

    public PermissionTreeNode(String id, String name, String code, String parentId) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.parentId = parentId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public List<PermissionTreeNode> getChildren() { return children; }
    public void setChildren(List<PermissionTreeNode> children) { this.children = children; }
}
