package com.platform.marketing.modules.linkedin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LinkedInService {

    @Value("${linkedin.api.client-id}")
    private String clientId;

    @Value("${linkedin.api.client-secret}")
    private String clientSecret;

    @Value("${linkedin.api.redirect-uri}")
    private String redirectUri;

    private static final String BASE_URL = "https://api.linkedin.com/v2/";
    private static final String AUTH_URL = "https://www.linkedin.com/oauth/v2/authorization";
    private static final String TOKEN_URL = "https://www.linkedin.com/oauth/v2/accessToken";
    
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateAuthUrl(String state) {
        String scope = "r_liteprofile r_emailaddress w_member_social";
        
        return AUTH_URL + "?" +
                "response_type=code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&state=" + state +
                "&scope=" + scope;
    }

    public String exchangeCodeForToken(String code, String state) {
        try {
            FormBody.Builder formBuilder = new FormBody.Builder()
                    .add("grant_type", "authorization_code")
                    .add("code", code)
                    .add("redirect_uri", redirectUri)
                    .add("client_id", clientId)
                    .add("client_secret", clientSecret);

            Request request = new Request.Builder()
                    .url(TOKEN_URL)
                    .post(formBuilder.build())
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    return jsonNode.get("access_token").asText();
                } else {
                    System.err.println("LinkedIn token exchange failed: " + response.body().string());
                    return null;
                }
            }
        } catch (Exception e) {
            System.err.println("Error exchanging LinkedIn code for token: " + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getUserProfile(String accessToken) {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "people/~:(id,firstName,lastName,profilePicture(displayImage~:playableStreams))")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    
                    Map<String, Object> profile = new HashMap<>();
                    profile.put("id", jsonNode.get("id").asText());
                    
                    if (jsonNode.has("firstName")) {
                        profile.put("firstName", jsonNode.get("firstName").get("localized").get("en_US").asText());
                    }
                    
                    if (jsonNode.has("lastName")) {
                        profile.put("lastName", jsonNode.get("lastName").get("localized").get("en_US").asText());
                    }
                    
                    return profile;
                } else {
                    System.err.println("Failed to get LinkedIn profile: " + response.body().string());
                    return null;
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting LinkedIn profile: " + e.getMessage());
            return null;
        }
    }

    public boolean sendConnectionRequest(String accessToken, String recipientId, String message) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("recipients", new String[]{"urn:li:person:" + recipientId});
            requestBody.put("message", message);
            requestBody.put("trackingId", "marketing-system-" + System.currentTimeMillis());

            String json = objectMapper.writeValueAsString(requestBody);
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(BASE_URL + "people/~~/connections")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Restli-Protocol-Version", "2.0.0")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    System.out.println("LinkedIn connection request sent successfully");
                    return true;
                } else {
                    System.err.println("LinkedIn connection request failed: " + response.body().string());
                    return false;
                }
            }
        } catch (Exception e) {
            System.err.println("Error sending LinkedIn connection request: " + e.getMessage());
            return false;
        }
    }

    public boolean sendMessage(String accessToken, String recipientId, String message) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("recipients", new String[]{"urn:li:person:" + recipientId});
            requestBody.put("messageType", "MEMBER_TO_MEMBER");
            requestBody.put("subject", "Marketing Message");
            requestBody.put("body", message);

            String json = objectMapper.writeValueAsString(requestBody);
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(BASE_URL + "messaging/conversations")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Restli-Protocol-Version", "2.0.0")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    System.out.println("LinkedIn message sent successfully");
                    return true;
                } else {
                    System.err.println("LinkedIn message failed: " + response.body().string());
                    return false;
                }
            }
        } catch (Exception e) {
            System.err.println("Error sending LinkedIn message: " + e.getMessage());
            return false;
        }
    }

    public Map<String, Object> createPost(String accessToken, String content, String visibility) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("author", "urn:li:person:" + getUserId(accessToken));
            requestBody.put("lifecycleState", "PUBLISHED");
            
            Map<String, Object> specificContent = new HashMap<>();
            Map<String, Object> shareCommentary = new HashMap<>();
            shareCommentary.put("text", content);
            
            Map<String, Object> shareContent = new HashMap<>();
            shareContent.put("shareCommentary", shareCommentary);
            shareContent.put("shareMediaCategory", "NONE");
            
            specificContent.put("com.linkedin.ugc.ShareContent", shareContent);
            requestBody.put("specificContent", specificContent);
            
            Map<String, Object> visibilityObj = new HashMap<>();
            visibilityObj.put("com.linkedin.ugc.MemberNetworkVisibility", visibility.toUpperCase());
            requestBody.put("visibility", visibilityObj);

            String json = objectMapper.writeValueAsString(requestBody);
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(BASE_URL + "ugcPosts")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Restli-Protocol-Version", "2.0.0")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("postId", jsonNode.get("id").asText());
                    result.put("createdAt", LocalDateTime.now());
                    
                    return result;
                } else {
                    System.err.println("LinkedIn post creation failed: " + response.body().string());
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("error", "Post creation failed");
                    return result;
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating LinkedIn post: " + e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", e.getMessage());
            return result;
        }
    }

    private String getUserId(String accessToken) {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "people/~:(id)")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    return jsonNode.get("id").asText();
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            return null;
        }
    }
}