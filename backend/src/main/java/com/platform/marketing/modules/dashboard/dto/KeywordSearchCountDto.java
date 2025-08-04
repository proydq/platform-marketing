package com.platform.marketing.modules.dashboard.dto;

public class KeywordSearchCountDto {
    private String keyword;
    private Long count;

    public KeywordSearchCountDto() {
    }

    public KeywordSearchCountDto(String keyword, Long count) {
        this.keyword = keyword;
        this.count = count;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
