package com.platform.marketing.modules.ai.dto;

public class AIRecommendationDto {
    private String id;
    private String type;
    private String title;
    private String description;
    private Double priority;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Double getPriority() {
        return priority;
    }

    public void setPriority(Double priority) {
        this.priority = priority;
    }
}