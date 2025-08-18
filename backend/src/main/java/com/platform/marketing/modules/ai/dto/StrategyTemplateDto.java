package com.platform.marketing.modules.ai.dto;

/**
 * 策略模板DTO
 */
public class StrategyTemplateDto {
    private String id;
    private String name;
    private String goal;
    private String industry;
    private String description;
    private String[] steps;
    private String[] resources;

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

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getSteps() {
        return steps;
    }

    public void setSteps(String[] steps) {
        this.steps = steps;
    }

    public String[] getResources() {
        return resources;
    }

    public void setResources(String[] resources) {
        this.resources = resources;
    }
}