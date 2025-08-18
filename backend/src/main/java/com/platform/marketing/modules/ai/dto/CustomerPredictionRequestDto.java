package com.platform.marketing.modules.ai.dto;

public class CustomerPredictionRequestDto {
    private String customerId;
    private String predictionType;
    private String[] dataPoints;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPredictionType() {
        return predictionType;
    }

    public void setPredictionType(String predictionType) {
        this.predictionType = predictionType;
    }

    public String[] getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(String[] dataPoints) {
        this.dataPoints = dataPoints;
    }
}