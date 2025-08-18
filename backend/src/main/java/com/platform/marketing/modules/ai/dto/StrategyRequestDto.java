package com.platform.marketing.modules.ai.dto;

/**
 * 策略生成请求DTO
 */
public class StrategyRequestDto {
    private String goal;              // 营销目标
    private String audience;          // 目标受众
    private String budget;            // 预算规模
    private String timeline;          // 时间周期
    private String industry;          // 行业
    private String currentSituation;  // 当前状况
    private String challenges;        // 面临挑战

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCurrentSituation() {
        return currentSituation;
    }

    public void setCurrentSituation(String currentSituation) {
        this.currentSituation = currentSituation;
    }

    public String getChallenges() {
        return challenges;
    }

    public void setChallenges(String challenges) {
        this.challenges = challenges;
    }
}