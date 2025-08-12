package com.platform.marketing.modules.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

@Service
public class OpenAIService {

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateContent(String prompt, String customerName, String companyName, String industry) {
        try {
            String fullPrompt = buildFullPrompt(prompt, customerName, companyName, industry);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", fullPrompt);
            requestBody.put("messages", new Object[]{message});
            requestBody.put("max_tokens", 1000);
            requestBody.put("temperature", 0.7);

            String json = objectMapper.writeValueAsString(requestBody);
            
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("OpenAI API request failed: " + response);
                }

                String responseBody = response.body().string();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                
                return jsonNode.path("choices")
                        .get(0)
                        .path("message")
                        .path("content")
                        .asText();
            }
        } catch (Exception e) {
            return "Failed to generate content: " + e.getMessage();
        }
    }

    private String buildFullPrompt(String template, String customerName, String companyName, String industry) {
        return String.format(
            "Generate a professional marketing email based on the following template and customer information:\n\n" +
            "Template: %s\n\n" +
            "Customer Information:\n" +
            "- Name: %s\n" +
            "- Company: %s\n" +
            "- Industry: %s\n\n" +
            "Please personalize the email content while maintaining a professional tone. " +
            "Make it engaging and relevant to their industry.",
            template, customerName, companyName, industry
        );
    }

    public String translateContent(String content, String targetLanguage) {
        try {
            String prompt = String.format(
                "Translate the following text to %s. Maintain the professional tone and formatting:\n\n%s",
                targetLanguage, content
            );

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            requestBody.put("messages", new Object[]{message});
            requestBody.put("max_tokens", 1000);
            requestBody.put("temperature", 0.3);

            String json = objectMapper.writeValueAsString(requestBody);
            
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("OpenAI API request failed: " + response);
                }

                String responseBody = response.body().string();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                
                return jsonNode.path("choices")
                        .get(0)
                        .path("message")
                        .path("content")
                        .asText();
            }
        } catch (Exception e) {
            return "Failed to translate content: " + e.getMessage();
        }
    }
}