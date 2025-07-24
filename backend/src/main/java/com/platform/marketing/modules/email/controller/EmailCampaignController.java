package com.platform.marketing.modules.email.controller;

import com.platform.marketing.modules.email.entity.EmailCampaignTemplate;
import com.platform.marketing.modules.email.entity.EmailCampaignRecord;
import com.platform.marketing.modules.email.service.EmailCampaignTemplateService;
import com.platform.marketing.modules.email.service.EmailCampaignRecordService;
import com.platform.marketing.util.ResponseEntity;
import com.platform.marketing.util.ResponsePageDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/email-campaign")
public class EmailCampaignController {

    private final EmailCampaignTemplateService templateService;
    private final EmailCampaignRecordService recordService;

    public EmailCampaignController(EmailCampaignTemplateService templateService,
                                   EmailCampaignRecordService recordService) {
        this.templateService = templateService;
        this.recordService = recordService;
    }

    // Template endpoints
    @GetMapping("/templates")
    @PreAuthorize("hasPermission('email-campaign:template:list')")
    public ResponseEntity<ResponsePageDataEntity<EmailCampaignTemplate>> listTemplates(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EmailCampaignTemplate> p = templateService.search(keyword, PageRequest.of(page, size));
        return ResponseEntity.success(new ResponsePageDataEntity<>(p.getTotalElements(), p.getContent()));
    }

    @PostMapping("/templates")
    @PreAuthorize("hasPermission('email-campaign:template:create')")
    public ResponseEntity<EmailCampaignTemplate> createTemplate(@RequestBody EmailCampaignTemplate template) {
        return ResponseEntity.success(templateService.create(template));
    }

    @PutMapping("/templates/{id}")
    @PreAuthorize("hasPermission('email-campaign:template:update')")
    public ResponseEntity<EmailCampaignTemplate> updateTemplate(@PathVariable String id,
                                                                @RequestBody EmailCampaignTemplate template) {
        return ResponseEntity.success(templateService.update(id, template));
    }

    @DeleteMapping("/templates/{id}")
    @PreAuthorize("hasPermission('email-campaign:template:delete')")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String id) {
        templateService.delete(id);
        return ResponseEntity.success(null);
    }

    // Records endpoints
    @GetMapping("/records")
    @PreAuthorize("hasPermission('email-campaign:record:list')")
    public ResponseEntity<ResponsePageDataEntity<EmailCampaignRecord>> listRecords(
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EmailCampaignRecord> p = recordService.search(status, PageRequest.of(page, size));
        return ResponseEntity.success(new ResponsePageDataEntity<>(p.getTotalElements(), p.getContent()));
    }

    @PostMapping("/records")
    @PreAuthorize("hasPermission('email-campaign:record:create')")
    public ResponseEntity<EmailCampaignRecord> createRecord(@RequestBody EmailCampaignRecord record) {
        return ResponseEntity.success(recordService.create(record));
    }

    @PostMapping("/test-send")
    @PreAuthorize("hasPermission('email-campaign:test-send')")
    public ResponseEntity<Void> testSend(@RequestBody Map<String, String> body) {
        // In real implementation we'd send an email here
        if (!body.containsKey("email")) {
            return ResponseEntity.fail(400, "email required");
        }
        return ResponseEntity.success(null);
    }
}
