package com.platform.marketing.modules.campaign.dto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.platform.marketing.modules.campaign.entity.MarketingCampaign;
public class MarketingCampaignDto {
    private String id;
    private String name;
    private String status;
    private List<String> channels;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private String createdBy;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public static MarketingCampaignDto fromEntity(MarketingCampaign entity) {
        MarketingCampaignDto dto = new MarketingCampaignDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setStatus(entity.getStatus());
        String channelStr = entity.getChannel();
        if (channelStr != null && !channelStr.trim().isEmpty()) {
            dto.setChannels(Arrays.asList(channelStr.split(",")));
        } else {
            dto.setChannels(Collections.emptyList());
        }
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setDescription(entity.getDescription());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setUpdatedTime(entity.getUpdatedTime());
        return dto;
    }

    public static void copyToEntity(MarketingCampaignDto dto, MarketingCampaign entity) {
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        if (dto.getChannels() != null && !dto.getChannels().isEmpty()) {
            entity.setChannel(String.join(",", dto.getChannels()));
        } else {
            entity.setChannel(null);
        }
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setDescription(dto.getDescription());
        entity.setCreatedBy(dto.getCreatedBy());
    }
}
