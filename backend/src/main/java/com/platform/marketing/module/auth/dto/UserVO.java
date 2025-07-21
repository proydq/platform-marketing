package com.platform.marketing.module.auth.dto;

import java.util.UUID;

public class UserVO {
    private UUID id;
    private String username;
    private String email;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
