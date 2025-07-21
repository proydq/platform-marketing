package com.platform.marketing.module.user.dto;

import java.util.UUID;

/**
 * Save DTO for creating or updating users.
 */
public class UserSaveDTO {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private Boolean status;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
}
