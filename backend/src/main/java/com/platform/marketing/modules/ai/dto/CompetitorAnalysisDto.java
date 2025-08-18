package com.platform.marketing.modules.ai.dto;

public class CompetitorAnalysisDto {
    private String name;
    private String marketShare;
    private String strengths;
    private String weaknesses;
    private String strategy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMarketShare() {
        return marketShare;
    }

    public void setMarketShare(String marketShare) {
        this.marketShare = marketShare;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
}