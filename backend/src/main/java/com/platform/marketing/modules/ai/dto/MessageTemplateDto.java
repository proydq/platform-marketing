package com.platform.marketing.modules.ai.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class MessageTemplateDto {
    
    private Long id;
    
    @NotBlank(message = "Template name is required")
    private String name;
    
    @NotBlank(message = "Type is required")
    private String type;
    
    @NotBlank(message = "Industry is required")
    private String industry;
    
    @NotBlank(message = "Language is required")
    private String language;
    
    @NotBlank(message = "Subject is required")
    private String subject;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    private String variables;
    
    private String category;
    
    @NotNull(message = "Active status is required")
    private Boolean isActive;
    
    private Integer usageCount;
    
    private Double successRate;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}

@Data
class ContentGenerationRequest {
    
    @NotBlank(message = "Template ID is required")
    private Long templateId;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    @NotBlank(message = "Industry is required")
    private String industry;
    
    private String targetLanguage; // 可选，用于翻译
    
    private String customPrompt; // 可选，自定义提示词
}

@Data
class ContentGenerationResponse {
    
    private String subject;
    
    private String content;
    
    private String originalLanguage;
    
    private String targetLanguage;
    
    private Long templateId;
    
    private String templateName;
}

@Data
class TemplateStatsDto {
    
    private Long templateId;
    
    private String templateName;
    
    private Integer totalUsage;
    
    private Integer successfulSends;
    
    private Integer openCount;
    
    private Integer replyCount;
    
    private Double openRate;
    
    private Double replyRate;
    
    private Double successRate;
}