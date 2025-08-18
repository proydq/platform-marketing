package com.platform.marketing.modules.ai.dto;

import java.util.List;

public class MarketTrendsDto {
    private String industry;
    private String region;
    private List<String> trends;
    private Double growthRate;
    private String forecast;

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<String> getTrends() {
        return trends;
    }

    public void setTrends(List<String> trends) {
        this.trends = trends;
    }

    public Double getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(Double growthRate) {
        this.growthRate = growthRate;
    }

    public String getForecast() {
        return forecast;
    }

    public void setForecast(String forecast) {
        this.forecast = forecast;
    }
}