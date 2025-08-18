package com.platform.marketing.modules.ai.dto;

/**
 * 内容优化请求DTO
 */
public class ContentOptimizationRequestDto {
    private String content;
    private String contentType;
    private String targetAudience;
    private String[] optimizationGoals;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String[] getOptimizationGoals() {
        return optimizationGoals;
    }

    public void setOptimizationGoals(String[] optimizationGoals) {
        this.optimizationGoals = optimizationGoals;
    }
}