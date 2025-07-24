package com.platform.marketing.modules.contentgeneration.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.marketing.modules.contentgeneration.entity.ContentGeneration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContentGenerationDto {
    private static final ObjectMapper mapper = new ObjectMapper();

    private String id;
    private String name;
    private List<String> channels = new ArrayList<>();
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<String> contents = new ArrayList<>();
    private String cycle;
    private String status;
    private LocalDateTime createdAt;

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

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static ContentGenerationDto fromEntity(ContentGeneration entity) {
        ContentGenerationDto dto = new ContentGenerationDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        String channelStr = entity.getChannels();
        if (channelStr != null && !channelStr.trim().isEmpty()) {
            dto.setChannels(Arrays.asList(channelStr.split(",")));
        }
        String contentsStr = entity.getContents();
        if (contentsStr != null && !contentsStr.trim().isEmpty()) {
            try {
                dto.setContents(mapper.readValue(contentsStr, new TypeReference<List<String>>(){}));
            } catch (Exception e) {
                dto.setContents(Arrays.asList(contentsStr.split("\\n")));
            }
        }
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setCycle(entity.getCycle());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public static void copyToEntity(ContentGenerationDto dto, ContentGeneration entity) {
        entity.setName(dto.getName());
        if (dto.getChannels() != null && !dto.getChannels().isEmpty()) {
            entity.setChannels(String.join(",", dto.getChannels()));
        } else {
            entity.setChannels("");
        }
        if (dto.getContents() != null && !dto.getContents().isEmpty()) {
            try {
                entity.setContents(mapper.writeValueAsString(dto.getContents()));
            } catch (Exception e) {
                entity.setContents(String.join("\n", dto.getContents()));
            }
        } else {
            entity.setContents("[]");
        }
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setCycle(dto.getCycle());
        entity.setStatus(dto.getStatus());
    }
}
