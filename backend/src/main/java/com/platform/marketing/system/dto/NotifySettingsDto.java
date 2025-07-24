package com.platform.marketing.system.dto;

public class NotifySettingsDto {
    private boolean enabled;
    private String type;
    private String channel;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
}
