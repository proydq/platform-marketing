package com.platform.marketing.module.permission.dto;

/**
 * Query DTO for permissions list.
 */
public class PermissionQueryDTO {
    private int page = 0;
    private int size = 10;
    private String keyword;
    private String type;
    private Boolean status;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
}
