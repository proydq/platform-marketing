package com.platform.marketing.modules.ai.dto;

import java.util.List;

/**
 * 营销策略DTO
 */
public class MarketingStrategyDto {
    private String id;                    // 策略ID
    private String name;                  // 策略名称
    private String title;                 // 策略标题
    private String description;           // 策略描述
    private String priority;              // 优先级(高优先级/中优先级/低优先级)
    private List<String> channels;        // 推荐渠道
    private Integer expectedROI;          // 预期ROI百分比
    private Integer difficulty;           // 执行难度(1-5)
    private String timeline;              // 执行时间线
    private List<String> actionItems;     // 行动项目
    private String targetAudience;        // 目标受众
    private String budget;                // 预算要求
    private List<String> kpis;           // 关键指标

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

    public Integer getExpectedROI() {
        return expectedROI;
    }

    public void setExpectedROI(Integer expectedROI) {
        this.expectedROI = expectedROI;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public List<String> getActionItems() {
        return actionItems;
    }

    public void setActionItems(List<String> actionItems) {
        this.actionItems = actionItems;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public List<String> getKpis() {
        return kpis;
    }

    public void setKpis(List<String> kpis) {
        this.kpis = kpis;
    }
}