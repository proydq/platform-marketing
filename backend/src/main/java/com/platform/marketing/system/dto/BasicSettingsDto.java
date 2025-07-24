package com.platform.marketing.system.dto;

public class BasicSettingsDto {
    private String siteName;
    private String brandColor;
    private String logoUrl;
    private String language;

    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }

    public String getBrandColor() { return brandColor; }
    public void setBrandColor(String brandColor) { this.brandColor = brandColor; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}
