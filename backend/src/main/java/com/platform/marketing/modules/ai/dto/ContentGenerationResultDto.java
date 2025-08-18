package com.platform.marketing.modules.ai.dto;

import java.util.List;

/**
 * 内容生成结果DTO
 */
public class ContentGenerationResultDto {
    private String type;                  // 内容类型
    private String subject;               // 邮件主题(仅邮件类型)
    private String content;               // 生成的内容
    private String language;              // 语言
    private Integer wordCount;            // 字数
    private Double qualityScore;          // 质量评分
    private List<String> suggestions;     // 优化建议
    private List<String> keywords;        // 包含的关键词
    private String tone;                  // 语调

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public Double getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(Double qualityScore) {
        this.qualityScore = qualityScore;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }
}