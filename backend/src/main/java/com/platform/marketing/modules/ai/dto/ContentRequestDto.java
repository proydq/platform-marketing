package com.platform.marketing.modules.ai.dto;

/**
 * 内容生成请求DTO
 */
public class ContentRequestDto {
    private String type;              // 内容类型
    private String customerSegment;   // 目标客户群体
    private String tone;              // 语调风格
    private String keyPoints;         // 关键信息点
    private String language;          // 目标语言
    private String productId;         // 相关产品ID(可选)
    private String campaignId;        // 相关活动ID(可选)
    private Integer maxLength;        // 最大长度

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCustomerSegment() {
        return customerSegment;
    }

    public void setCustomerSegment(String customerSegment) {
        this.customerSegment = customerSegment;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public String getKeyPoints() {
        return keyPoints;
    }

    public void setKeyPoints(String keyPoints) {
        this.keyPoints = keyPoints;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }
}