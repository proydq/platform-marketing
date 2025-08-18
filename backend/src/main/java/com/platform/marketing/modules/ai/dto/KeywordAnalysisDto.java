package com.platform.marketing.modules.ai.dto;

/**
 * 关键词分析结果DTO
 */
public class KeywordAnalysisDto {
    private String keyword;              // 关键词
    private String searchVolume;         // 月搜索量
    private String competition;          // 竞争度(低/中等/高)
    private Double trend;                // 趋势百分比
    private Integer difficulty;          // SEO难度(0-100)
    private String suggestion;           // AI建议
    private Double relevanceScore;       // 相关度评分
    private String[] relatedKeywords;    // 相关关键词
    private String marketingPotential;   // 营销潜力评估

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSearchVolume() {
        return searchVolume;
    }

    public void setSearchVolume(String searchVolume) {
        this.searchVolume = searchVolume;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public Double getTrend() {
        return trend;
    }

    public void setTrend(Double trend) {
        this.trend = trend;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public Double getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(Double relevanceScore) {
        this.relevanceScore = relevanceScore;
    }

    public String[] getRelatedKeywords() {
        return relatedKeywords;
    }

    public void setRelatedKeywords(String[] relatedKeywords) {
        this.relatedKeywords = relatedKeywords;
    }

    public String getMarketingPotential() {
        return marketingPotential;
    }

    public void setMarketingPotential(String marketingPotential) {
        this.marketingPotential = marketingPotential;
    }
}