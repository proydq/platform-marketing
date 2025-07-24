package com.platform.marketing.system.dto;

public class SecuritySettingsDto {
    private String passwordStrength;
    private Integer deviceLimit;
    private boolean twoFactor;

    public String getPasswordStrength() { return passwordStrength; }
    public void setPasswordStrength(String passwordStrength) { this.passwordStrength = passwordStrength; }

    public Integer getDeviceLimit() { return deviceLimit; }
    public void setDeviceLimit(Integer deviceLimit) { this.deviceLimit = deviceLimit; }

    public boolean isTwoFactor() { return twoFactor; }
    public void setTwoFactor(boolean twoFactor) { this.twoFactor = twoFactor; }
}
