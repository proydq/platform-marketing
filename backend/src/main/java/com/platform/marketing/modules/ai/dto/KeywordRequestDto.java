package com.platform.marketing.modules.ai.dto;

import java.util.List;

/**
 * 关键词分析请求DTO
 */
public class KeywordRequestDto {
    private String industry;           // 目标行业
    private String market;            // 目标市场
    private String seedKeywords;      // 种子关键词
    private List<String> analysisGoals; // 分析目标
    private String language;          // 语言
    private Integer maxResults;       // 最大结果数

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getSeedKeywords() {
        return seedKeywords;
    }

    public void setSeedKeywords(String seedKeywords) {
        this.seedKeywords = seedKeywords;
    }

    public List<String> getAnalysisGoals() {
        return analysisGoals;
    }

    public void setAnalysisGoals(List<String> analysisGoals) {
        this.analysisGoals = analysisGoals;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }
}