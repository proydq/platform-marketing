package com.platform.marketing.modules.ai.controller;

import com.platform.marketing.modules.ai.dto.MessageTemplateDto;
import com.platform.marketing.modules.ai.entity.MessageTemplate;
import com.platform.marketing.modules.ai.service.MessageTemplateService;
import com.platform.marketing.modules.ai.service.OpenAIService;
import com.platform.marketing.modules.customer.entity.Customer;
import com.platform.marketing.modules.customer.service.CustomerService;
import com.platform.marketing.util.ResponsePageDataEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIContentController {
    
    @Autowired
    private OpenAIService openAIService;
    
    @Autowired
    private MessageTemplateService templateService;
    
    @Autowired
    private CustomerService customerService;
    
    @GetMapping("/templates")
    public ResponseEntity<ResponsePageDataEntity<MessageTemplate>> getTemplates(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageTemplate> templates = templateService.findTemplatesWithFilters(
                type, industry, language, category, pageable);
        
        return ResponseEntity.ok(new ResponsePageDataEntity<MessageTemplate>(
                templates.getTotalElements(), templates.getContent()));
    }
    
    @PostMapping("/templates")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<MessageTemplate>> createTemplate(
            @Valid @RequestBody MessageTemplateDto dto) {
        
        MessageTemplate created = templateService.create(dto);
        return ResponseEntity.ok(
                com.platform.marketing.util.ResponseEntity.success("Template created successfully", created));
    }
    
    @PutMapping("/templates/{id}")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<MessageTemplate>> updateTemplate(
            @PathVariable Long id, @Valid @RequestBody MessageTemplateDto dto) {
        
        MessageTemplate updated = templateService.update(id, dto);
        return ResponseEntity.ok(
                com.platform.marketing.util.ResponseEntity.success("Template updated successfully", updated));
    }
    
    @DeleteMapping("/templates/{id}")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<String>> deleteTemplate(@PathVariable Long id) {
        templateService.delete(id);
        return ResponseEntity.ok(
                com.platform.marketing.util.ResponseEntity.success("Template deleted successfully", null));
    }
    
    @PostMapping("/generate-content")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<Map<String, String>>> generateContent(
            @RequestBody Map<String, Object> request) {
        
        Long templateId = Long.valueOf(request.get("templateId").toString());
        String customerId = request.get("customerId").toString();
        String targetLanguage = (String) request.get("targetLanguage");
        
        Optional<MessageTemplate> templateOpt = templateService.findById(templateId);
        if (!templateOpt.isPresent()) {
            return ResponseEntity.badRequest().body(
                    com.platform.marketing.util.ResponseEntity.error("Template not found"));
        }
        
        Optional<Customer> customerOpt = customerService.findById(customerId);
        if (!customerOpt.isPresent()) {
            return ResponseEntity.badRequest().body(
                    com.platform.marketing.util.ResponseEntity.error("Customer not found"));
        }
        
        MessageTemplate template = templateOpt.get();
        Customer customer = customerOpt.get();
        
        String generatedContent = openAIService.generateContent(
                template.getContent(),
                customer.getName(),
                customer.getName(), // Using name as company name placeholder
                "General" // Default industry
        );
        
        String finalContent = generatedContent;
        String finalSubject = template.getSubject();
        
        if (targetLanguage != null && !targetLanguage.equals(template.getLanguage())) {
            finalContent = openAIService.translateContent(generatedContent, targetLanguage);
            finalSubject = openAIService.translateContent(template.getSubject(), targetLanguage);
        }
        
        Map<String, String> result = new HashMap<>();
        result.put("subject", finalSubject);
        result.put("content", finalContent);
        result.put("templateName", template.getName());
        result.put("originalLanguage", template.getLanguage());
        result.put("targetLanguage", targetLanguage != null ? targetLanguage : template.getLanguage());
        
        templateService.updateUsageStats(templateId, true);
        
        return ResponseEntity.ok(
                com.platform.marketing.util.ResponseEntity.success("Content generated successfully", result));
    }
    
    @GetMapping("/templates/recommended")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<List<MessageTemplate>>> getRecommendedTemplates(
            @RequestParam String type,
            @RequestParam String industry,
            @RequestParam(defaultValue = "en") String language) {
        
        List<MessageTemplate> templates = templateService.findRecommendedTemplates(type, industry, language);
        return ResponseEntity.ok(
                com.platform.marketing.util.ResponseEntity.success("Recommended templates retrieved", templates));
    }
    
    @GetMapping("/templates/top-performing")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<List<MessageTemplate>>> getTopPerformingTemplates(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<MessageTemplate> templates = templateService.findTopPerformingTemplates(limit);
        return ResponseEntity.ok(
                com.platform.marketing.util.ResponseEntity.success("Top performing templates retrieved", templates));
    }
    
    @GetMapping("/metadata/industries")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<List<String>>> getIndustries() {
        List<String> industries = templateService.getDistinctIndustries();
        return ResponseEntity.ok(
                com.platform.marketing.util.ResponseEntity.success("Industries retrieved", industries));
    }
    
    @GetMapping("/metadata/categories")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<List<String>>> getCategories() {
        List<String> categories = templateService.getDistinctCategories();
        return ResponseEntity.ok(
                com.platform.marketing.util.ResponseEntity.success("Categories retrieved", categories));
    }
}