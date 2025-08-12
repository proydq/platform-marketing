package com.platform.marketing.modules.linkedin.controller;

import com.platform.marketing.modules.linkedin.service.LinkedInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/linkedin")
@CrossOrigin(origins = "*")
public class LinkedInController {

    @Autowired
    private LinkedInService linkedInService;

    private final Map<String, String> stateStore = new ConcurrentHashMap<>();

    @GetMapping("/auth")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<Map<String, String>>> initiateAuth() {
        String state = UUID.randomUUID().toString();
        String authUrl = linkedInService.generateAuthUrl(state);
        
        stateStore.put(state, "pending");
        
        Map<String, String> response = new HashMap<>();
        response.put("authUrl", authUrl);
        response.put("state", state);
        
        return ResponseEntity.ok(
                new com.platform.marketing.util.ResponseEntity<>("success", "Authorization URL generated", response));
    }

    @GetMapping("/callback")
    public RedirectView handleCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            @RequestParam(value = "error", required = false) String error) {
        
        if (error != null) {
            return new RedirectView("http://localhost:3000/linkedin-error?error=" + error);
        }
        
        if (!stateStore.containsKey(state)) {
            return new RedirectView("http://localhost:3000/linkedin-error?error=invalid_state");
        }
        
        String accessToken = linkedInService.exchangeCodeForToken(code, state);
        
        if (accessToken != null) {
            stateStore.put(state, accessToken);
            return new RedirectView("http://localhost:3000/linkedin-success?state=" + state);
        } else {
            return new RedirectView("http://localhost:3000/linkedin-error?error=token_exchange_failed");
        }
    }

    @GetMapping("/token/{state}")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<String>> getToken(@PathVariable String state) {
        String token = stateStore.get(state);
        
        if (token == null || "pending".equals(token)) {
            return ResponseEntity.badRequest().body(
                    new com.platform.marketing.util.ResponseEntity<>("error", "Token not available", null));
        }
        
        return ResponseEntity.ok(
                new com.platform.marketing.util.ResponseEntity<>("success", "Token retrieved", token));
    }

    @GetMapping("/profile")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<Map<String, Object>>> getProfile(
            @RequestHeader("Authorization") String authHeader) {
        
        String accessToken = extractToken(authHeader);
        if (accessToken == null) {
            return ResponseEntity.badRequest().body(
                    new com.platform.marketing.util.ResponseEntity<>("error", "Invalid authorization header", null));
        }
        
        Map<String, Object> profile = linkedInService.getUserProfile(accessToken);
        
        if (profile != null) {
            return ResponseEntity.ok(
                    new com.platform.marketing.util.ResponseEntity<>("success", "Profile retrieved", profile));
        } else {
            return ResponseEntity.internalServerError().body(
                    new com.platform.marketing.util.ResponseEntity<>("error", "Failed to retrieve profile", null));
        }
    }

    @PostMapping("/send-connection-request")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<String>> sendConnectionRequest(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        
        String accessToken = extractToken(authHeader);
        if (accessToken == null) {
            return ResponseEntity.badRequest().body(
                    new com.platform.marketing.util.ResponseEntity<>("error", "Invalid authorization header", null));
        }
        
        String recipientId = request.get("recipientId");
        String message = request.get("message");
        
        if (recipientId == null || message == null) {
            return ResponseEntity.badRequest().body(
                    new com.platform.marketing.util.ResponseEntity<>("error", "Missing recipientId or message", null));
        }
        
        boolean success = linkedInService.sendConnectionRequest(accessToken, recipientId, message);
        
        if (success) {
            return ResponseEntity.ok(
                    new com.platform.marketing.util.ResponseEntity<>("success", "Connection request sent", "Request sent successfully"));
        } else {
            return ResponseEntity.internalServerError().body(
                    new com.platform.marketing.util.ResponseEntity<>("error", "Failed to send connection request", null));
        }
    }

    @PostMapping("/send-message")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<String>> sendMessage(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        
        String accessToken = extractToken(authHeader);
        if (accessToken == null) {
            return ResponseEntity.badRequest().body(
                    new com.platform.marketing.util.ResponseEntity<>("error", "Invalid authorization header", null));
        }
        
        String recipientId = request.get("recipientId");
        String message = request.get("message");
        
        if (recipientId == null || message == null) {
            return ResponseEntity.badRequest().body(
                    new com.platform.marketing.util.ResponseEntity<>("error", "Missing recipientId or message", null));
        }
        
        boolean success = linkedInService.sendMessage(accessToken, recipientId, message);
        
        if (success) {
            return ResponseEntity.ok(
                    new com.platform.marketing.util.ResponseEntity<>("success", "Message sent successfully", "Message delivered"));
        } else {
            return ResponseEntity.internalServerError().body(
                    new com.platform.marketing.util.ResponseEntity<>("error", "Failed to send message", null));
        }
    }

    @PostMapping("/create-post")
    public ResponseEntity<com.platform.marketing.util.ResponseEntity<Map<String, Object>>> createPost(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        
        String accessToken = extractToken(authHeader);
        if (accessToken == null) {
            return ResponseEntity.badRequest().body(
                    new com.platform.marketing.util.ResponseEntity<>("error", "Invalid authorization header", null));
        }
        
        String content = request.get("content");
        String visibility = request.getOrDefault("visibility", "PUBLIC");
        
        if (content == null) {
            return ResponseEntity.badRequest().body(
                    new com.platform.marketing.util.ResponseEntity<>("error", "Content is required", null));
        }
        
        Map<String, Object> result = linkedInService.createPost(accessToken, content, visibility);
        
        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(
                    new com.platform.marketing.util.ResponseEntity<>("success", "Post created successfully", result));
        } else {
            return ResponseEntity.internalServerError().body(
                    new com.platform.marketing.util.ResponseEntity<>("error", "Failed to create post", result));
        }
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}