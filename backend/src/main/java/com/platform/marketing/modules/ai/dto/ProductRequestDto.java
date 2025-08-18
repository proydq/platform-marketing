package com.platform.marketing.modules.ai.dto;

import java.math.BigDecimal;

/**
 * 产品推荐请求DTO
 */
public class ProductRequestDto {
    private String customerType;       // 客户类型
    private String purchaseHistory;    // 购买历史描述
    private BigDecimal budgetMin;      // 最低预算
    private BigDecimal budgetMax;      // 最高预算
    private String industry;           // 目标行业
    private String preferences;        // 客户偏好
    private Integer maxResults;        // 最大结果数

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getPurchaseHistory() {
        return purchaseHistory;
    }

    public void setPurchaseHistory(String purchaseHistory) {
        this.purchaseHistory = purchaseHistory;
    }

    public BigDecimal getBudgetMin() {
        return budgetMin;
    }

    public void setBudgetMin(BigDecimal budgetMin) {
        this.budgetMin = budgetMin;
    }

    public BigDecimal getBudgetMax() {
        return budgetMax;
    }

    public void setBudgetMax(BigDecimal budgetMax) {
        this.budgetMax = budgetMax;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }
}