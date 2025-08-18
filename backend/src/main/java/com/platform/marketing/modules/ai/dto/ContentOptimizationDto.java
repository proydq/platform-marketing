package com.platform.marketing.modules.ai.dto;

import java.util.List;

/**
 * 内容优化DTO
 */
public class ContentOptimizationDto {
    private String optimizedContent;
    private List<String> suggestions;
    private Double improvementScore;
    private String[] issues;
    private String[] strengths;

    public String getOptimizedContent() {
        return optimizedContent;
    }

    public void setOptimizedContent(String optimizedContent) {
        this.optimizedContent = optimizedContent;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public Double getImprovementScore() {
        return improvementScore;
    }

    public void setImprovementScore(Double improvementScore) {
        this.improvementScore = improvementScore;
    }

    public String[] getIssues() {
        return issues;
    }

    public void setIssues(String[] issues) {
        this.issues = issues;
    }

    public String[] getStrengths() {
        return strengths;
    }

    public void setStrengths(String[] strengths) {
        this.strengths = strengths;
    }
}