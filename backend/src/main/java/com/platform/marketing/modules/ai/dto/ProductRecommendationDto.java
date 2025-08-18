package com.platform.marketing.modules.ai.dto;

import java.math.BigDecimal;

/**
 * 产品推荐DTO
 */
public class ProductRecommendationDto {
    private String id;                 // 产品ID
    private String name;               // 产品名称
    private BigDecimal price;          // 价格
    private String image;              // 产品图片
    private Double matchScore;         // 匹配度评分(1-5)
    private String reason;             // 推荐理由
    private String category;           // 产品分类
    private String description;        // 产品描述
    private String[] features;         // 产品特点
    private Double profitMargin;       // 利润率
    private String recommendationType; // 推荐类型(popular/personalized/trending)

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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(Double matchScore) {
        this.matchScore = matchScore;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getFeatures() {
        return features;
    }

    public void setFeatures(String[] features) {
        this.features = features;
    }

    public Double getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(Double profitMargin) {
        this.profitMargin = profitMargin;
    }

    public String getRecommendationType() {
        return recommendationType;
    }

    public void setRecommendationType(String recommendationType) {
        this.recommendationType = recommendationType;
    }
}