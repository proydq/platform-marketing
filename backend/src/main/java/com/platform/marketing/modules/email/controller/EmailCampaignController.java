package com.platform.marketing.modules.email.controller;

import com.platform.marketing.modules.email.dto.EmailSendRequest;
import com.platform.marketing.modules.email.entity.EmailSendRecord;
import com.platform.marketing.modules.email.service.EmailService;
import com.platform.marketing.util.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * \uD83D\uDCE7 \u90AE\u4EF6\u8425\u9500\u63A7\u5236\u5668
 * \u529F\u80FD\u8BF4\u660E\uFF1A
 * 1. sendEmail: \u53D1\u9001\u90AE\u4EF6
 * 2. testSendEmail: \u6D4B\u8BD5\u53D1\u9001\uFF08\u56FA\u5B9A\u6536\u4EF6\u4EBA\uFF09
 * 3. uploadRecipients: \u4E0A\u4F20CSV\u6587\u4EF6\u63D0\u53D6\u6536\u4EF6\u4EBA\u90AE\u7BB1
 * 4. getRecords: \u5206\u9875\u83B7\u53D6\u53D1\u9001\u8BB0\u5F55
 */
@RestController
@RequestMapping("/v1/email-campaign")
public class EmailCampaignController {

    private final EmailService emailService;

    public EmailCampaignController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, Boolean>> sendEmail(@RequestBody EmailSendRequest request) {
        if (request.getSubject() == null || request.getSubject().trim().isEmpty()
            || request.getContent() == null || request.getContent().trim().isEmpty()
            || request.getToList() == null || request.getToList().isEmpty()) {
            return ResponseEntity.fail(400, "subject, content and toList are required");
        }
        emailService.sendEmail(request.getSubject(), request.getContent(), request.getToList());
        return ResponseEntity.success(Collections.singletonMap("success", true));
    }

    @PostMapping("/test-send")
    public ResponseEntity<Map<String, Boolean>> testSendEmail() {
        // \u9ED8\u8BA4\u6D4B\u8BD5\u6536\u4EF6\u4EBA\uFF08\u5F00\u53D1\u53EF\u66FF\u6362\uFF09
        List<String> toList = List.of("480075988@qq.com");
        emailService.sendEmail("\u6D4B\u8BD5\u90AE\u4EF6", "\u8FD9\u662F\u4E00\u5C01\u6D4B\u8BD5\u90AE\u4EF6", toList);
        return ResponseEntity.success(Collections.singletonMap("success", true));
    }

    @PostMapping("/upload-recipients")
    public ResponseEntity<Map<String, List<String>>> uploadRecipients(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.fail(400, "file is empty");
        }
        List<String> emails = emailService.parseRecipients(file);
        Map<String, List<String>> resp = new HashMap<>();
        resp.put("emails", emails);
        return ResponseEntity.success(resp);
    }

    @GetMapping("/records")
    public ResponseEntity<Map<String, Object>> getSendRecords(@RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        Page<EmailSendRecord> resultPage = emailService.getRecords(page, size);
        Map<String, Object> result = new HashMap<>();
        result.put("list", resultPage.getContent());
        result.put("total", resultPage.getTotalElements());
        return ResponseEntity.success(result);
    }
}

