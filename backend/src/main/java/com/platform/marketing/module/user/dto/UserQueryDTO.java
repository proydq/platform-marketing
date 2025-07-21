package com.platform.marketing.module.user.dto;

/**
 * Query DTO for fetching users with pagination.
 */
public class UserQueryDTO {
    private int page = 0;
    private int size = 10;
    private String keyword;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
}
