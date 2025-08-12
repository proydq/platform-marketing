package com.platform.marketing.modules.whatsapp.controller;

import com.platform.marketing.modules.whatsapp.service.WhatsAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/whatsapp")
@CrossOrigin(origins = "*")
public class WhatsAppController {

    @Autowired
    private WhatsAppService whatsAppService;

    @PostMapping("/send-text")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<String>> sendTextMessage(
            @RequestBody Map<String, String> request) {
        
        String to = request.get("to");
        String message = request.get("message");
        
        if (to == null || message == null) {
            return ResponseEntity.badRequest().body(
                    new com.platform.marketing.util.ResponseEntity<>("error", "Missing required fields: to, message", null));
        }
        
        boolean success = whatsAppService.sendTextMessage(to, message);
        
        if (success) {
            return ResponseEntity.ok(
                    new com.platform.marketing.util.ResponseEntity<>("success", "Message sent successfully", "Message delivered"));
        } else {
            return ResponseEntity.internalServerError().body(
                    new com.platform.marketing.util.ResponseEntity<>("error", "Failed to send message", null));
        }
    }

    @PostMapping("/send-template")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<String>> sendTemplateMessage(
            @RequestBody Map<String, Object> request) {
        
        String to = (String) request.get("to");
        String templateName = (String) request.get("templateName");
        String languageCode = (String) request.get("languageCode");
        @SuppressWarnings("unchecked")
        Map<String, String> parameters = (Map<String, String>) request.get("parameters");
        
        if (to == null || templateName == null || languageCode == null) {
            return ResponseEntity.badRequest().body(
                    new com.platform.marketing.util.ResponseEntity<>("error", "Missing required fields: to, templateName, languageCode", null));
        }
        
        boolean success = whatsAppService.sendTemplateMessage(to, templateName, languageCode, parameters);
        
        if (success) {
            return ResponseEntity.ok(
                    new com.platform.marketing.util.ResponseEntity<>("success", "Template message sent successfully", "Message delivered"));
        } else {
            return ResponseEntity.internalServerError().body(
                    new com.platform.marketing.util.ResponseEntity<>("error", "Failed to send template message", null));
        }
    }

    @PostMapping("/send-campaign")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<Map<String, Object>>> sendCampaignMessages(
            @RequestBody Map<String, Object> request) {
        
        @SuppressWarnings("unchecked")
        java.util.List<String> recipients = (java.util.List<String>) request.get("recipients");
        String message = (String) request.get("message");
        String templateName = (String) request.get("templateName");
        String languageCode = (String) request.get("languageCode");
        
        if (recipients == null || recipients.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new com.platform.marketing.util.ResponseEntity<>("error", "Recipients list cannot be empty", null));
        }
        
        Map<String, Object> results = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        
        for (String recipient : recipients) {
            boolean success;
            
            if (templateName != null && languageCode != null) {
                success = whatsAppService.sendTemplateMessage(recipient, templateName, languageCode, null);
            } else if (message != null) {
                success = whatsAppService.sendTextMessage(recipient, message);
            } else {
                failCount++;
                continue;
            }
            
            if (success) {
                successCount++;
            } else {
                failCount++;
            }
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        results.put("totalSent", recipients.size());
        results.put("successCount", successCount);
        results.put("failCount", failCount);
        results.put("successRate", (double) successCount / recipients.size() * 100);
        
        return ResponseEntity.ok(
                new com.platform.marketing.util.ResponseEntity<>("success", "Campaign completed", results));
    }

    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {
        
        if (whatsAppService.verifyWebhook(mode, token, challenge)) {
            return ResponseEntity.ok(challenge);
        } else {
            return ResponseEntity.badRequest().body("Verification failed");
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String body,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature) {
        
        String result = whatsAppService.handleWebhook(body, signature);
        return ResponseEntity.ok(result);
    }
}