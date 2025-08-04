package com.platform.marketing.modules.email.controller;

import com.platform.marketing.modules.email.dto.EmailSendRequest;
import com.platform.marketing.modules.email.service.EmailService;
import com.platform.marketing.util.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, Boolean>> sendEmail(@RequestBody EmailSendRequest request) {
        if (request.getSubject() == null || request.getSubject().trim().isEmpty() ||
            request.getContent() == null || request.getContent().trim().isEmpty() ||
            request.getToList() == null || request.getToList().isEmpty()) {
            return ResponseEntity.fail(400, "subject, content and toList are required");
        }
        emailService.sendEmail(request.getSubject(), request.getContent(), request.getToList());
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.success(resp);
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
}
