package com.platform.marketing.modules.whatsapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class WhatsAppService {

    @Value("${whatsapp.business.api.token}")
    private String apiToken;

    @Value("${whatsapp.business.api.phone-number-id}")
    private String phoneNumberId;

    private static final String BASE_URL = "https://graph.facebook.com/v18.0/";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean sendTemplateMessage(String to, String templateName, String languageCode, Map<String, String> parameters) {
        try {
            String url = BASE_URL + phoneNumberId + "/messages";
            
            Map<String, Object> requestBody = buildTemplateMessageRequest(to, templateName, languageCode, parameters);
            String json = objectMapper.writeValueAsString(requestBody);
            
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + apiToken)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return true;
                } else {
                    System.err.println("WhatsApp API error: " + response.body().string());
                    return false;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to send WhatsApp message: " + e.getMessage());
            return false;
        }
    }

    public boolean sendTextMessage(String to, String message) {
        try {
            String url = BASE_URL + phoneNumberId + "/messages";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("messaging_product", "whatsapp");
            requestBody.put("recipient_type", "individual");
            requestBody.put("to", to);
            requestBody.put("type", "text");
            Map<String, String> textMap = new HashMap<>();
            textMap.put("body", message);
            requestBody.put("text", textMap);
            
            String json = objectMapper.writeValueAsString(requestBody);
            
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + apiToken)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return true;
                } else {
                    System.err.println("WhatsApp API error: " + response.body().string());
                    return false;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to send WhatsApp text message: " + e.getMessage());
            return false;
        }
    }

    public String handleWebhook(String body, String signature) {
        try {
            JsonNode json = objectMapper.readTree(body);
            
            if (json.has("entry")) {
                JsonNode entry = json.get("entry").get(0);
                if (entry.has("changes")) {
                    JsonNode changes = entry.get("changes").get(0);
                    if (changes.has("value")) {
                        JsonNode value = changes.get("value");
                        
                        if (value.has("messages")) {
                            return processIncomingMessage(value.get("messages").get(0));
                        } else if (value.has("statuses")) {
                            return processMessageStatus(value.get("statuses").get(0));
                        }
                    }
                }
            }
            
            return "EVENT_RECEIVED";
        } catch (Exception e) {
            System.err.println("Error processing WhatsApp webhook: " + e.getMessage());
            return "ERROR";
        }
    }

    private Map<String, Object> buildTemplateMessageRequest(String to, String templateName, 
                                                           String languageCode, Map<String, String> parameters) {
        Map<String, Object> request = new HashMap<>();
        request.put("messaging_product", "whatsapp");
        request.put("recipient_type", "individual");
        request.put("to", to);
        request.put("type", "template");
        
        Map<String, Object> template = new HashMap<>();
        template.put("name", templateName);
        
        Map<String, String> language = new HashMap<>();
        language.put("code", languageCode);
        template.put("language", language);
        
        if (parameters != null && !parameters.isEmpty()) {
            // Build template components with parameters
            // This would need to be customized based on your template structure
        }
        
        request.put("template", template);
        return request;
    }

    private String processIncomingMessage(JsonNode message) {
        String from = message.get("from").asText();
        String messageText = "";
        
        if (message.has("text")) {
            messageText = message.get("text").get("body").asText();
        }
        
        System.out.println("Received message from " + from + ": " + messageText);
        
        return "MESSAGE_PROCESSED";
    }

    private String processMessageStatus(JsonNode status) {
        String messageId = status.get("id").asText();
        String statusType = status.get("status").asText();
        
        System.out.println("Message " + messageId + " status: " + statusType);
        
        return "STATUS_PROCESSED";
    }

    public boolean verifyWebhook(String mode, String token, String challenge) {
        String verifyToken = System.getenv("WHATSAPP_WEBHOOK_TOKEN");
        
        if ("subscribe".equals(mode) && verifyToken.equals(token)) {
            System.out.println("WhatsApp webhook verified successfully");
            return true;
        }
        
        return false;
    }
}