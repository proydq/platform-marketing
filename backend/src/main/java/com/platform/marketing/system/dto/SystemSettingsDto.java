package com.platform.marketing.system.dto;

public class SystemSettingsDto {
    private BasicSettingsDto basicSettings;
    private NotifySettingsDto notificationSettings;
    private SecuritySettingsDto securitySettings;

    public BasicSettingsDto getBasicSettings() { return basicSettings; }
    public void setBasicSettings(BasicSettingsDto basicSettings) { this.basicSettings = basicSettings; }

    public NotifySettingsDto getNotificationSettings() { return notificationSettings; }
    public void setNotificationSettings(NotifySettingsDto notificationSettings) { this.notificationSettings = notificationSettings; }

    public SecuritySettingsDto getSecuritySettings() { return securitySettings; }
    public void setSecuritySettings(SecuritySettingsDto securitySettings) { this.securitySettings = securitySettings; }
}
