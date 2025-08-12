package com.platform.marketing.modules.ai.service.impl;

import com.platform.marketing.modules.ai.dto.MessageTemplateDto;
import com.platform.marketing.modules.ai.entity.MessageTemplate;
import com.platform.marketing.modules.ai.repository.MessageTemplateRepository;
import com.platform.marketing.modules.ai.service.MessageTemplateService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MessageTemplateServiceImpl implements MessageTemplateService {
    
    @Autowired
    private MessageTemplateRepository templateRepository;
    
    @Override
    public Page<MessageTemplate> findTemplatesWithFilters(String type, String industry, 
                                                        String language, String category, Pageable pageable) {
        return templateRepository.findTemplatesWithFilters(type, industry, language, category, pageable);
    }
    
    @Override
    public Optional<MessageTemplate> findById(Long id) {
        return templateRepository.findById(id);
    }
    
    @Override
    public MessageTemplate create(MessageTemplateDto dto) {
        MessageTemplate template = new MessageTemplate();
        BeanUtils.copyProperties(dto, template);
        return templateRepository.save(template);
    }
    
    @Override
    public MessageTemplate update(Long id, MessageTemplateDto dto) {
        MessageTemplate existing = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + id));
        
        BeanUtils.copyProperties(dto, existing, "id", "createdAt", "usageCount");
        return templateRepository.save(existing);
    }
    
    @Override
    public void delete(Long id) {
        MessageTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + id));
        template.setIsActive(false);
        templateRepository.save(template);
    }
    
    @Override
    public List<MessageTemplate> findRecommendedTemplates(String type, String industry, String language) {
        List<MessageTemplate> templates = templateRepository.findByIndustryAndTypeAndLanguageAndIsActiveTrue(
                industry, type, language);
        
        if (templates.isEmpty()) {
            templates = templateRepository.findByTypeAndLanguageAndIsActiveTrue(type, language);
        }
        
        return templates;
    }
    
    @Override
    public List<MessageTemplate> findTopPerformingTemplates(int limit) {
        return templateRepository.findTopPerformingTemplates(PageRequest.of(0, limit));
    }
    
    @Override
    public List<String> getDistinctIndustries() {
        return templateRepository.findDistinctIndustries();
    }
    
    @Override
    public List<String> getDistinctCategories() {
        return templateRepository.findDistinctCategories();
    }
    
    @Override
    public void updateUsageStats(Long templateId, boolean success) {
        Optional<MessageTemplate> templateOpt = templateRepository.findById(templateId);
        if (templateOpt.isPresent()) {
            MessageTemplate template = templateOpt.get();
            template.setUsageCount(template.getUsageCount() + 1);
            
            if (success && template.getSuccessRate() != null) {
                double currentSuccessCount = template.getSuccessRate() * (template.getUsageCount() - 1);
                double newSuccessRate = (currentSuccessCount + 1) / template.getUsageCount();
                template.setSuccessRate(newSuccessRate);
            }
            
            templateRepository.save(template);
        }
    }
}