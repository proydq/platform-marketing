package com.platform.marketing.modules.ai.service;

import com.platform.marketing.modules.ai.dto.MessageTemplateDto;
import com.platform.marketing.modules.ai.entity.MessageTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface MessageTemplateService {
    
    Page<MessageTemplate> findTemplatesWithFilters(String type, String industry, 
                                                  String language, String category, Pageable pageable);
    
    Optional<MessageTemplate> findById(Long id);
    
    MessageTemplate create(MessageTemplateDto dto);
    
    MessageTemplate update(Long id, MessageTemplateDto dto);
    
    void delete(Long id);
    
    List<MessageTemplate> findRecommendedTemplates(String type, String industry, String language);
    
    List<MessageTemplate> findTopPerformingTemplates(int limit);
    
    List<String> getDistinctIndustries();
    
    List<String> getDistinctCategories();
    
    void updateUsageStats(Long templateId, boolean success);
}